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

package org.springframework.data.redis.connection.jedis;

import java.util.*;

import org.springframework.data.redis.ClusterStateFailureException;
import org.springframework.data.redis.connection.ClusterTopology;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.convert.Converters;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.github.wendy512.easyboot.redis.config.NodeConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author wendy512
 * @date 2022-05-20 14:41:38
 * @since 1.0.0
 */
public class JedisClusterTopologyProviderExtension extends JedisClusterConnection.JedisClusterTopologyProvider {

    private final Object lock = new Object();
    private final JedisCluster cluster;
    private long time = 0;
    private ClusterTopology cached;
    private final Map<String, NodeConfig> nodeConfigMap = new HashMap<>();
    
    /**
     * Create new {@link JedisClusterConnection.JedisClusterTopologyProvider}
     *
     * @param cluster
     */
    public JedisClusterTopologyProviderExtension(JedisCluster cluster, List<NodeConfig> nodeConfigs) {
        super(cluster);
        this.cluster = cluster;
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

    @Override
    public ClusterTopology getTopology() {
        if (cached != null && time + 100 > System.currentTimeMillis()) {
            return cached;
        }

        Map<String, Exception> errors = new LinkedHashMap<>();

        List<Map.Entry<String, JedisPool>> list = new ArrayList<>(cluster.getClusterNodes().entrySet());
        Collections.shuffle(list);

        for (Map.Entry<String, JedisPool> entry : list) {

            try (Jedis jedis = entry.getValue().getResource()) {

                time = System.currentTimeMillis();
                String clusterNodes = replaceClusterNodes(jedis.clusterNodes());
                Set<RedisClusterNode> nodes = Converters.toSetOfRedisClusterNodes(clusterNodes);
                synchronized (lock) {
                    cached = new ClusterTopology(nodes);
                }
                return cached;
            } catch (Exception ex) {
                errors.put(entry.getKey(), ex);
            }
        }

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Exception> entry : errors.entrySet()) {
            sb.append(String.format("\r\n\t- %s failed: %s", entry.getKey(), entry.getValue().getMessage()));
        }

        throw new ClusterStateFailureException(
                "Could not retrieve cluster information. CLUSTER NODES returned with error." + sb.toString());
    }
    
    private String replaceClusterNodes(String clusterNodes) {
        String clusterNodesBak = clusterNodes;
        String[] lines = clusterNodes.split("\n");
        for (String line : lines) {
            String[] args = line.split(" ");
            String hostAndPort = args[1];
            // 排除bus 端口
            if (hostAndPort.indexOf("@") != -1) {
                hostAndPort = hostAndPort.substring(0, hostAndPort.lastIndexOf("@"));
            }

            NodeConfig nodeConfig = nodeConfigMap.get(hostAndPort.trim());
            if (null != nodeConfig) {
                clusterNodesBak = clusterNodesBak.replaceAll(hostAndPort, nodeConfig.getNode());
            }
        }
        
        return clusterNodesBak;
    }
}
