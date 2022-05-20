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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.github.wendy512.redis.config.NodeConfig;
import io.github.wendy512.redis.core.JedisClusterExtension;
import io.github.wendy512.redis.core.RedisClusterConnectionProxy;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

/**
 * JedisConnectionFactory拓展，详见{@link JedisConnectionFactory}
 * @author wendy512
 * @date 2022-05-20 11:12:45
 * @since 1.0.0
 */
public class JedisConnectionFactoryExtension extends JedisConnectionFactory {
    
    private List<NodeConfig> nodeConfig;
    

    public JedisConnectionFactoryExtension() {
        super();
    }

    @Deprecated
    public JedisConnectionFactoryExtension(JedisShardInfo shardInfo) {
        super(shardInfo);
    }

    public JedisConnectionFactoryExtension(JedisPoolConfig poolConfig) {
        super((poolConfig));
    }

    public JedisConnectionFactoryExtension(RedisSentinelConfiguration sentinelConfig) {
        super(sentinelConfig);
    }

    public JedisConnectionFactoryExtension(RedisSentinelConfiguration sentinelConfig, JedisPoolConfig poolConfig) {
        super(sentinelConfig, poolConfig);
    }

    public JedisConnectionFactoryExtension(RedisClusterConfiguration clusterConfig) {
        super(clusterConfig);
    }

    public JedisConnectionFactoryExtension(RedisClusterConfiguration clusterConfig, JedisPoolConfig poolConfig) {
        super(clusterConfig, poolConfig);
    }

    public JedisConnectionFactoryExtension(RedisStandaloneConfiguration standaloneConfig) {
        super(standaloneConfig);
    }

    public JedisConnectionFactoryExtension(RedisStandaloneConfiguration standaloneConfig, JedisClientConfiguration clientConfig) {
        super(standaloneConfig, clientConfig);
    }

    public JedisConnectionFactoryExtension(RedisSentinelConfiguration sentinelConfig, JedisClientConfiguration clientConfig) {
        super(sentinelConfig, clientConfig);
    }

    public JedisConnectionFactoryExtension(RedisClusterConfiguration clusterConfig, JedisClientConfiguration clientConfig) {
        super(clusterConfig, clientConfig);
    }

    public List<NodeConfig> getNodeConfig() {
        return nodeConfig;
    }

    public void setNodeConfig(List<NodeConfig> nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    @Override
    protected JedisCluster createCluster(RedisClusterConfiguration clusterConfig, GenericObjectPoolConfig poolConfig) {
        Assert.notNull(clusterConfig, "Cluster configuration must not be null!");

        Set<HostAndPort> hostAndPort = new HashSet<>();
        for (RedisNode node : clusterConfig.getClusterNodes()) {
            hostAndPort.add(new HostAndPort(node.getHost(), node.getPort()));
        }

        int redirects = clusterConfig.getMaxRedirects() != null ? clusterConfig.getMaxRedirects() : 5;

        int connectTimeout = Math.toIntExact(getClientConfiguration().getConnectTimeout().toMillis());
        int readTimeout = Math.toIntExact(getClientConfiguration().getReadTimeout().toMillis());

        return StringUtils.hasText(getPassword())
                ? new JedisClusterExtension(hostAndPort, connectTimeout, readTimeout, redirects, getPassword(), poolConfig, getNodeConfig())
                : new JedisClusterExtension(hostAndPort, connectTimeout, readTimeout, redirects, poolConfig, getNodeConfig());
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        RedisClusterConnection clusterConnection = super.getClusterConnection();
        return new RedisClusterConnectionProxy(clusterConnection, (JedisCluster) clusterConnection.getNativeConnection(), nodeConfig);
    }
}
