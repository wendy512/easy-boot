package io.github.wendy512.redis.core;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * connection factory 拓展
 * @author taowenwu
 * @date 2021-04-30 16:18:16:18
 * @since 1.0.0
 */
public class JedisConnectionFactoryExtension extends JedisConnectionFactory {
    private RedisConfiguration redisConfiguration;
    private JedisClientConfiguration clientConfiguration;

    public JedisConnectionFactoryExtension() {}

    public JedisConnectionFactoryExtension(RedisStandaloneConfiguration standaloneConfiguration, JedisClientConfiguration clientConfiguration) {
        super(standaloneConfiguration, clientConfiguration);
        this.redisConfiguration = standaloneConfiguration;
    }

    public JedisConnectionFactoryExtension(RedisSentinelConfiguration sentinelConfiguration, JedisClientConfiguration clientConfiguration) {
        super(sentinelConfiguration, clientConfiguration);
    }

    public JedisConnectionFactoryExtension(RedisClusterConfiguration clusterConfiguration, JedisClientConfiguration clientConfiguration) {
        super(clusterConfiguration, clientConfiguration);
    }

    public RedisConfiguration getRedisConfiguration() {
        return redisConfiguration;
    }

    public void setRedisConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    public JedisClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public void setClientConfiguration(JedisClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }
}
