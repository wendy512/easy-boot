/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.wendy512.easyboot.redis.core.extension;

import io.github.wendy512.easyboot.redis.config.NodeConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JedisClusterInfoCache拓展，详见{@link JedisClusterInfoCache}
 * @author wendy512
 * @date 2022-05-20 09:59:26
 * @since 1.0.0
 */
public class JedisClusterInfoCacheExtension {
    private final Map<String, JedisPool> nodes = new HashMap<String, JedisPool>();
    private final Map<Integer, JedisPool> slots = new HashMap<Integer, JedisPool>();

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    private volatile boolean rediscovering;
    private final GenericObjectPoolConfig poolConfig;

    private int connectionTimeout;
    private int soTimeout;
    private String password;
    private String clientName;

    private final Map<String, NodeConfig> nodeConfigMap = new HashMap<>();

    private static final int MASTER_NODE_INDEX = 2;

    public JedisClusterInfoCacheExtension(final GenericObjectPoolConfig poolConfig, int timeout, List<NodeConfig> nodeConfigs) {
        this(poolConfig, timeout, timeout, null, null, nodeConfigs);
    }

    public JedisClusterInfoCacheExtension(final GenericObjectPoolConfig poolConfig,
                                 final int connectionTimeout, final int soTimeout, final String password, final String clientName, List<NodeConfig> nodeConfigs) {
        this.poolConfig = poolConfig;
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
        this.password = password;
        this.clientName = clientName;
        Assert.notEmpty(nodeConfigs, "nodeConfigs must not be null!");
        resetNodeConfigMap(nodeConfigs);
    }

    private void resetNodeConfigMap(List<NodeConfig> nodeConfigs) {
        for (NodeConfig config : nodeConfigs) {
            if (!StringUtils.isEmpty(config.getContainerNode())) {
                nodeConfigMap.put(config.getContainerNode(), config);
            }
        }
    }

    public void discoverClusterNodesAndSlots(Jedis jedis) {
        w.lock();

        try {
            reset();
            List<Object> slots = jedis.clusterSlots();

            for (Object slotInfoObj : slots) {
                List<Object> slotInfo = (List<Object>) slotInfoObj;

                if (slotInfo.size() <= MASTER_NODE_INDEX) {
                    continue;
                }

                List<Integer> slotNums = getAssignedSlotArray(slotInfo);

                // hostInfos
                int size = slotInfo.size();
                for (int i = MASTER_NODE_INDEX; i < size; i++) {
                    List<Object> hostInfos = (List<Object>) slotInfo.get(i);
                    if (hostInfos.size() <= 0) {
                        continue;
                    }

                    HostAndPort targetNode = generateHostAndPort(hostInfos);
                    setupNodeIfNotExist(targetNode);
                    if (i == MASTER_NODE_INDEX) {
                        assignSlotsToNode(slotNums, targetNode);
                    }
                }
            }
        } finally {
            w.unlock();
        }
    }

    public void renewClusterSlots(Jedis jedis) {
        //If rediscovering is already in process - no need to start one more same rediscovering, just return
        if (!rediscovering) {
            try {
                w.lock();
                if (!rediscovering) {
                    rediscovering = true;

                    try {
                        if (jedis != null) {
                            try {
                                discoverClusterSlots(jedis);
                                return;
                            } catch (JedisException e) {
                                //try nodes from all pools
                            }
                        }

                        for (Map.Entry<String,JedisPool> jp : nodes.entrySet()) {
                            Jedis j = null;
                            try {
                                j = jp.getValue().getResource();
                                discoverClusterSlots(j);
                                return;
                            } catch (JedisConnectionException e) {
                                // try next nodes
                            } finally {
                                if (j != null) {
                                    j.close();
                                }
                            }
                        }
                    } finally {
                        rediscovering = false;
                    }
                }
            } finally {
                w.unlock();
            }
        }
    }

    private void discoverClusterSlots(Jedis jedis) {
        List<Object> slots = jedis.clusterSlots();
        this.slots.clear();

        for (Object slotInfoObj : slots) {
            List<Object> slotInfo = (List<Object>) slotInfoObj;

            if (slotInfo.size() <= MASTER_NODE_INDEX) {
                continue;
            }

            List<Integer> slotNums = getAssignedSlotArray(slotInfo);

            // hostInfos
            List<Object> hostInfos = (List<Object>) slotInfo.get(MASTER_NODE_INDEX);
            if (hostInfos.isEmpty()) {
                continue;
            }

            // at this time, we just use master, discard slave information
            HostAndPort targetNode = generateHostAndPort(hostInfos);
            assignSlotsToNode(slotNums, targetNode);
        }
    }

    private HostAndPort generateHostAndPort(List<Object> hostInfos) {
        return new  HostAndPort(SafeEncoder.encode((byte[]) hostInfos.get(0)),
                ((Long) hostInfos.get(1)).intValue());
    }

    public JedisPool setupNodeIfNotExist(HostAndPort node) {
        w.lock();
        try {
            String nodeKey = getNodeKey(node);
            JedisPool existingPool = nodes.get(nodeKey);
            if (existingPool != null) return existingPool;

            // 主要改动这里，获取容器的隐射地址，如果不为空，则重新赋值host和port
            NodeConfig nodeConfig = nodeConfigMap.get(nodeKey);
            String host = node.getHost();
            int port = node.getPort();
            
            if (null != nodeConfig) {
                String[] hostAndPort = nodeConfig.getNode().split(":");
                host = hostAndPort[0];
                port = Integer.parseInt(hostAndPort[1]);
                nodeKey = nodeConfig.getNode();
            }
            
            JedisPool nodePool = new JedisPool(poolConfig, host, port,
                    connectionTimeout, soTimeout, password, 0, clientName, false, null, null, null);
            nodes.put(nodeKey, nodePool);
            return nodePool;
        } finally {
            w.unlock();
        }
    }

    public JedisPool setupNodeIfNotExist(HostAndPort node, boolean ssl) {
        w.lock();
        try {
            String nodeKey = getNodeKey(node);
            JedisPool existingPool = nodes.get(nodeKey);
            if (existingPool != null) return existingPool;

            // 主要改动这里，获取容器的隐射地址，如果不为空，则重新赋值host和port
            NodeConfig nodeConfig = nodeConfigMap.get(nodeKey);
            String host = node.getHost();
            int port = node.getPort();

            if (null != nodeConfig) {
                String[] hostAndPort = nodeConfig.getNode().split(":");
                host = hostAndPort[0];
                port = Integer.parseInt(hostAndPort[1]);
                nodeKey = nodeConfig.getNode();
            }
            
            JedisPool nodePool = new JedisPool(poolConfig, host, port,
                    connectionTimeout, soTimeout, password, 0, null, ssl, null, null, null);
            nodes.put(nodeKey, nodePool);
            return nodePool;
        } finally {
            w.unlock();
        }
    }

    public JedisPool setupNodeIfNotExist(HostAndPort node, boolean ssl, SSLSocketFactory sslSocketFactory,
                                         SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        w.lock();
        try {
            String nodeKey = getNodeKey(node);
            JedisPool existingPool = nodes.get(nodeKey);
            if (existingPool != null) return existingPool;

            // 主要改动这里，获取容器的隐射地址，如果不为空，则重新赋值host和port
            NodeConfig nodeConfig = nodeConfigMap.get(nodeKey);
            String host = node.getHost();
            int port = node.getPort();

            if (null != nodeConfig) {
                String[] hostAndPort = nodeConfig.getNode().split(":");
                host = hostAndPort[0];
                port = Integer.parseInt(hostAndPort[1]);
                nodeKey = nodeConfig.getNode();
            }
            JedisPool nodePool = new JedisPool(poolConfig, host, port,
                    connectionTimeout, soTimeout, password, 0, null, ssl, sslSocketFactory, sslParameters,
                    hostnameVerifier);
            nodes.put(nodeKey, nodePool);
            return nodePool;
        } finally {
            w.unlock();
        }
    }


    public void assignSlotToNode(int slot, HostAndPort targetNode) {
        w.lock();
        try {
            JedisPool targetPool = setupNodeIfNotExist(targetNode);
            slots.put(slot, targetPool);
        } finally {
            w.unlock();
        }
    }

    public void assignSlotsToNode(List<Integer> targetSlots, HostAndPort targetNode) {
        w.lock();
        try {
            JedisPool targetPool = setupNodeIfNotExist(targetNode);
            for (Integer slot : targetSlots) {
                slots.put(slot, targetPool);
            }
        } finally {
            w.unlock();
        }
    }

    public JedisPool getNode(String nodeKey) {
        r.lock();
        try {
            return nodes.get(nodeKey);
        } finally {
            r.unlock();
        }
    }

    public JedisPool getSlotPool(int slot) {
        r.lock();
        try {
            return slots.get(slot);
        } finally {
            r.unlock();
        }
    }

    public Map<String, JedisPool> getNodes() {
        r.lock();
        try {
            return new HashMap<String, JedisPool>(nodes);
        } finally {
            r.unlock();
        }
    }

    public List<JedisPool> getShuffledNodesPool() {
        r.lock();
        try {
            List<JedisPool> pools = new ArrayList<JedisPool>(nodes.values());
            Collections.shuffle(pools);
            return pools;
        } finally {
            r.unlock();
        }
    }

    /**
     * Clear discovered nodes collections and gently release allocated resources
     */
    public void reset() {
        w.lock();
        try {
            for (JedisPool pool : nodes.values()) {
                try {
                    if (pool != null) {
                        pool.destroy();
                    }
                } catch (Exception e) {
                    // pass
                }
            }
            nodes.clear();
            slots.clear();
        } finally {
            w.unlock();
        }
    }

    public static String getNodeKey(HostAndPort hnp) {
        return hnp.getHost() + ":" + hnp.getPort();
    }

    public static String getNodeKey(Client client) {
        return client.getHost() + ":" + client.getPort();
    }

    public static String getNodeKey(Jedis jedis) {
        return getNodeKey(jedis.getClient());
    }

    private List<Integer> getAssignedSlotArray(List<Object> slotInfo) {
        List<Integer> slotNums = new ArrayList<Integer>();
        for (int slot = ((Long) slotInfo.get(0)).intValue(); slot <= ((Long) slotInfo.get(1))
                .intValue(); slot++) {
            slotNums.add(slot);
        }
        return slotNums;
    }
}
