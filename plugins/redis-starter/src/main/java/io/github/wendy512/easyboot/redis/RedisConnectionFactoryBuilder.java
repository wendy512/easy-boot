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

package io.github.wendy512.easyboot.redis;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactoryExtension;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.github.wendy512.easyboot.redis.config.InstanceConfig;
import io.github.wendy512.easyboot.redis.config.NodeConfig;
import io.github.wendy512.easyboot.redis.config.PoolConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 默认实现
 * 
 * @author taowenwu
 * @date 2021-04-25 15:54:15:54
 * @since 1.0.0
 */
public class RedisConnectionFactoryBuilder {

    public static class $RedisConnectionFactoryBuilder {
        private RedisMode mode;
        private String clientName;
        private PoolConfig pool;
        private InstanceConfig instance;
        private boolean enableNodeMapping;

        public $RedisConnectionFactoryBuilder mode(RedisMode mode) {
            this.mode = mode;
            return this;
        }

        public $RedisConnectionFactoryBuilder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public $RedisConnectionFactoryBuilder poolConfig(PoolConfig pool) {
            this.pool = pool;
            return this;
        }

        public $RedisConnectionFactoryBuilder instanceConfig(InstanceConfig instance) {
            this.instance = instance;
            return this;
        }

        public $RedisConnectionFactoryBuilder enableNodeMapping(boolean enableNodeMapping) {
            this.enableNodeMapping = enableNodeMapping;
            return this;
        }

        public RedisConnectionFactory build() {
            JedisConnectionFactory connectionFactory;
            if (RedisMode.SINGLE == this.mode) {
                connectionFactory = createSingleRedisConnectionFactory();
            } else if (RedisMode.SENTINEL == this.mode) {
                connectionFactory = createSentinelRedisConnectionFactory();
            } else {
                connectionFactory = createClusterConnectionFactory();
            }
            connectionFactory.afterPropertiesSet();
            return connectionFactory;
        }

        public RedisConnectionConfiguration configuration() {
            RedisConnectionConfiguration configuration;
            if (RedisMode.SINGLE == this.mode) {
                configuration = new RedisConnectionConfiguration(mode, getRedisStandaloneConfiguration(),
                    jedisClientConfiguration());
            } else if (RedisMode.SENTINEL == this.mode) {
                configuration =
                    new RedisConnectionConfiguration(mode, getRedisSentinelConfiguration(), jedisClientConfiguration());
            } else {
                configuration =
                    new RedisConnectionConfiguration(mode, getRedisClusterConfiguration(), jedisClientConfiguration());
            }
            return configuration;
        }

        public JedisClientConfiguration jedisClientConfiguration() {
            return getJedisClientConfiguration();
        }

        private JedisConnectionFactory createSingleRedisConnectionFactory() {
            RedisStandaloneConfiguration config = getRedisStandaloneConfiguration();
            return new JedisConnectionFactoryExtension(config, getJedisClientConfiguration());
        }

        private RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
            RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(instance.getHost(), instance.getPort());
            if (null != instance.getDatabase()) {
                config.setDatabase(instance.getDatabase());
            }
            config.setPassword(instance.getPassword());
            return config;
        }

        private JedisClientConfiguration getJedisClientConfiguration() {
            // 获得默认的连接池构造器(怎么设计的，为什么不抽象出单独类，供用户使用呢)
            JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();
            JedisClientConfiguration.JedisClientConfigurationBuilder clientBuilder = builder.poolConfig(pool).and()
                .usePooling().and().connectTimeout(Duration.ofMillis(instance.getTimeout()))
                .readTimeout(Duration.ofMillis(instance.getTimeout()));
            if (!StringUtils.isEmpty(clientName)) {
                clientBuilder.clientName(clientName);
            }
            return clientBuilder.build();
        }

        private JedisConnectionFactory createSentinelRedisConnectionFactory() {
            Assert.notNull(instance.getNodes(), "redis.instances.xx.nodes 配置不能为空");
            RedisSentinelConfiguration config = getRedisSentinelConfiguration();
            JedisConnectionFactory connectionFactory = null;
            if (enableNodeMapping) {
                connectionFactory = new JedisConnectionFactoryExtension(config, getJedisClientConfiguration());
                ((JedisConnectionFactoryExtension) connectionFactory).setNodeConfig(instance.getNodes());
            } else {
                connectionFactory = new JedisConnectionFactory(config, getJedisClientConfiguration());
            }
            return connectionFactory;
        }

        private RedisSentinelConfiguration getRedisSentinelConfiguration() {
            List<NodeConfig> sentinelNodes = instance.getNodes();
            Set<String> sentinelHostAndPorts =
                sentinelNodes.stream().map(s -> s.getNode().trim()).collect(Collectors.toSet());
            RedisSentinelConfiguration config =
                new RedisSentinelConfiguration(instance.getMaster(), sentinelHostAndPorts);
            config.setPassword(instance.getPassword());
            return config;
        }

        private JedisConnectionFactory createClusterConnectionFactory() {
            RedisClusterConfiguration config = getRedisClusterConfiguration();
            JedisConnectionFactory connectionFactory = null;
            if (enableNodeMapping) {
                connectionFactory = new JedisConnectionFactoryExtension(config, getJedisClientConfiguration());
                ((JedisConnectionFactoryExtension) connectionFactory).setNodeConfig(instance.getNodes());
            } else {
                connectionFactory = new JedisConnectionFactory(config, getJedisClientConfiguration());
            }
            return connectionFactory;
        }

        private RedisClusterConfiguration getRedisClusterConfiguration() {
            Set<String> clusterNodes =
                instance.getNodes().stream().map(s -> s.getNode().trim()).collect(Collectors.toSet());
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterNodes);
            config.setPassword(instance.getPassword());
            return config;
        }
    }

    public static $RedisConnectionFactoryBuilder builder() {
        return new $RedisConnectionFactoryBuilder();
    }

    @Data
    @AllArgsConstructor
    public static class RedisConnectionConfiguration {
        private RedisMode mode;
        private RedisConfiguration redisConfiguration;
        private JedisClientConfiguration clientConfiguration;
    }
}
