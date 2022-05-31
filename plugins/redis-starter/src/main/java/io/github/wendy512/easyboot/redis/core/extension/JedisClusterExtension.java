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
import redis.clients.jedis.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 *  JedisCluster拓展，详见{@link redis.clients.jedis.JedisCluster}
 * @author wendy512
 * @date 2022-05-20 09:37:23
 * @since 1.0.0
 */
public class JedisClusterExtension extends JedisCluster {
    
    protected final List<NodeConfig> nodeConfigs;
    
    public JedisClusterExtension(HostAndPort node, List<NodeConfig> nodeConfigs) {
        super(node);
        this.nodeConfigs = nodeConfigs;
    }

    public JedisClusterExtension(Set<HostAndPort> nodes, int timeout, List<NodeConfig> nodeConfigs) {
        this(nodes, timeout, DEFAULT_MAX_REDIRECTIONS, new GenericObjectPoolConfig(), nodeConfigs);
    }

    public JedisClusterExtension(Set<HostAndPort> nodes, List<NodeConfig> nodeConfigs) {
        this(nodes, DEFAULT_TIMEOUT, nodeConfigs);
    }

    public JedisClusterExtension(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts,
                              final GenericObjectPoolConfig poolConfig, List<NodeConfig> nodeConfigs) {
        super(jedisClusterNode, timeout, maxAttempts, poolConfig);
        this.connectionHandler = new JedisSlotBasedConnectionHandlerExtension(jedisClusterNode, poolConfig,
                timeout, nodeConfigs);
        this.nodeConfigs = nodeConfigs;
    }

    public JedisClusterExtension(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
        int maxAttempts, final GenericObjectPoolConfig poolConfig, List<NodeConfig> nodeConfigs) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig);
        this.connectionHandler = new JedisSlotBasedConnectionHandlerExtension(jedisClusterNode, poolConfig,
                connectionTimeout, soTimeout, nodeConfigs);
        this.nodeConfigs = nodeConfigs;
    }

    public JedisClusterExtension(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
        int maxAttempts, String password, GenericObjectPoolConfig poolConfig, List<NodeConfig> nodeConfigs) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
        this.connectionHandler = new JedisSlotBasedConnectionHandlerExtension(jedisClusterNode, poolConfig,
                connectionTimeout, soTimeout, password, nodeConfigs);
        this.nodeConfigs = nodeConfigs;
    }

    public JedisClusterExtension(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
        int maxAttempts, String password, String clientName, GenericObjectPoolConfig poolConfig, List<NodeConfig> nodeConfigs) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, clientName, poolConfig);
        this.connectionHandler = new JedisSlotBasedConnectionHandlerExtension(jedisClusterNode, poolConfig,
                connectionTimeout, soTimeout, password, clientName, nodeConfigs);
        this.nodeConfigs = nodeConfigs;
    }

    @Override
    public Map<String, JedisPool> getClusterNodes() {
        return this.connectionHandler.getNodes();
    }
}
