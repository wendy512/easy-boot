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

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.wendy512.easyboot.redis.config.NodeConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisNoReachableClusterNodeException;

/**
 * JedisClusterConnectionHandler拓展，详见{@link JedisSlotBasedConnectionHandler}
 * @author wendy512
 * @date 2022-05-20 09:55:37
 * @since 1.0.0
 */
public class JedisSlotBasedConnectionHandlerExtension extends JedisSlotBasedConnectionHandler {
    protected final JedisClusterInfoCacheExtension cache;
    
    public JedisSlotBasedConnectionHandlerExtension(Set<HostAndPort> nodes,
                                           final GenericObjectPoolConfig poolConfig, int timeout, List<NodeConfig> nodeConfigs) {
        this(nodes, poolConfig, timeout, timeout, nodeConfigs);
    }

    public JedisSlotBasedConnectionHandlerExtension(Set<HostAndPort> nodes,
                                           final GenericObjectPoolConfig poolConfig, int connectionTimeout, int soTimeout, List<NodeConfig> nodeConfigs) {
        this(nodes, poolConfig, connectionTimeout, soTimeout, null, nodeConfigs);
    }

    public JedisSlotBasedConnectionHandlerExtension(Set<HostAndPort> nodes,
                                         final GenericObjectPoolConfig poolConfig, int connectionTimeout, int soTimeout, String password, List<NodeConfig> nodeConfigs) {
        this(nodes, poolConfig, connectionTimeout, soTimeout, password, null, nodeConfigs);
    }

    public JedisSlotBasedConnectionHandlerExtension(Set<HostAndPort> nodes,
                                         final GenericObjectPoolConfig poolConfig, int connectionTimeout, int soTimeout, String password, String clientName, List<NodeConfig> nodeConfigs) {
        super(nodes, poolConfig, connectionTimeout, soTimeout, password, clientName);
        this.cache = new JedisClusterInfoCacheExtension(poolConfig, connectionTimeout, soTimeout, password, clientName, nodeConfigs);
        initializeSlotsCache(nodes, poolConfig, connectionTimeout, soTimeout, password, clientName);
    }

    public Jedis getConnection() {
        // In antirez's redis-rb-cluster implementation,
        // getRandomConnection always return valid connection (able to
        // ping-pong)
        // or exception if all connections are invalid

        List<JedisPool> pools = cache.getShuffledNodesPool();

        for (JedisPool pool : pools) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();

                if (jedis == null) {
                    continue;
                }

                String result = jedis.ping();

                if (result.equalsIgnoreCase("pong")) return jedis;

                jedis.close();
            } catch (JedisException ex) {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

        throw new JedisNoReachableClusterNodeException("No reachable node in cluster");
    }

    public Jedis getConnectionFromSlot(int slot) {
        JedisPool connectionPool = cache.getSlotPool(slot);
        if (connectionPool != null) {
            // It can't guaranteed to get valid connection because of node
            // assignment
            return connectionPool.getResource();
        } else {
            renewSlotCache(); //It's abnormal situation for cluster mode, that we have just nothing for slot, try to rediscover state
            connectionPool = cache.getSlotPool(slot);
            if (connectionPool != null) {
                return connectionPool.getResource();
            } else {
                //no choice, fallback to new connection to random node
                return getConnection();
            }
        }
    }

    public Jedis getConnectionFromNode(HostAndPort node) {
        return cache.setupNodeIfNotExist(node).getResource();
    }

    public Map<String, JedisPool> getNodes() {
        return cache.getNodes();
    }

    private void initializeSlotsCache(Set<HostAndPort> startNodes, GenericObjectPoolConfig poolConfig,
                                      int connectionTimeout, int soTimeout, String password, String clientName) {
        for (HostAndPort hostAndPort : startNodes) {
            Jedis jedis = null;
            try {
                jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout, soTimeout);
                if (password != null) {
                    jedis.auth(password);
                }
                if (clientName != null) {
                    jedis.clientSetname(clientName);
                }
                cache.discoverClusterNodesAndSlots(jedis);
                break;
            } catch (JedisConnectionException e) {
                e.printStackTrace();
                // try next nodes
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public void renewSlotCache() {
        cache.renewClusterSlots(null);
    }

    public void renewSlotCache(Jedis jedis) {
        cache.renewClusterSlots(jedis);
    }

    public void close() {
        cache.reset();
    }
}
