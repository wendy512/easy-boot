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

package io.github.wendy512.redis.core;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterTopologyProviderExtension;

import io.github.wendy512.redis.config.NodeConfig;
import redis.clients.jedis.JedisCluster;

/**
 * {@link RedisClusterConnection} 代理
 * @author wendy512
 * @date 2022-05-20 14:36:23
 * @since 1.0.0
 */
public class RedisClusterConnectionProxy extends JedisClusterConnection {
    private final RedisClusterConnection connection;
    private final JedisClusterTopologyProviderExtension topologyProvider;

    public RedisClusterConnectionProxy(RedisClusterConnection connection, JedisCluster cluster,
        List<NodeConfig> nodeConfigs) {
        super(cluster);
        this.connection = connection;
        this.topologyProvider = new JedisClusterTopologyProviderExtension(cluster, nodeConfigs);
    }

    @Override
    public Set<RedisClusterNode> clusterGetNodes() {
        return topologyProvider.getTopology().getNodes();
    }
}
