package io.github.wendy512.redis;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.github.wendy512.redis.config.InstanceConfig;
import io.github.wendy512.redis.config.NodeConfig;
import io.github.wendy512.redis.config.PoolConfig;
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
            return new JedisConnectionFactory(config, getJedisClientConfiguration());
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
            Assert.notNull(instance.getSentinelNodes(), "redis.instances.xx.sentinelNodes 配置不能为空");
            RedisSentinelConfiguration config = getRedisSentinelConfiguration();
            return new JedisConnectionFactory(config, getJedisClientConfiguration());
        }

        private RedisSentinelConfiguration getRedisSentinelConfiguration() {
            List<NodeConfig> sentinelNodes = instance.getSentinelNodes();
            Set<String> sentinelHostAndPorts =
                sentinelNodes.stream().map(s -> s.getNode().trim()).collect(Collectors.toSet());
            RedisSentinelConfiguration config =
                new RedisSentinelConfiguration(instance.getMaster(), sentinelHostAndPorts);
            config.setPassword(instance.getPassword());
            return config;
        }

        private JedisConnectionFactory createClusterConnectionFactory() {
            RedisClusterConfiguration config = getRedisClusterConfiguration();
            return new JedisConnectionFactory(config, getJedisClientConfiguration());
        }

        private RedisClusterConfiguration getRedisClusterConfiguration() {
            Set<String> clusterNodes =
                instance.getClusterNodes().stream().map(s -> s.getNode().trim()).collect(Collectors.toSet());
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
