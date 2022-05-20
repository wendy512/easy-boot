package io.github.wendy512.redis.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis 应用上下文
 * 
 * @author taowenwu
 * @date 2021-05-06 08:57:8:57
 * @since 1.0.0
 */
public class RedisApplicationContext {
    private final Map<String, RedisTemplate> TEMPLATES = new ConcurrentHashMap<>();
    private final Map<String, RedisConnectionFactory> CONNECTION_FACTORIES = new ConcurrentHashMap<>();
    public static final String DEFAULT_NAME = "default";

    public RedisTemplate getRedisTemplate(String instanceName) {
        return TEMPLATES.get(instanceName);
    }

    public RedisConnectionFactory getRedisConnectionFactory(String instanceName) {
        return CONNECTION_FACTORIES.get(instanceName);
    }

    public RedisTemplate getRedisTemplate() {
        return TEMPLATES.get(DEFAULT_NAME);
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return CONNECTION_FACTORIES.get(DEFAULT_NAME);
    }

    public void putRedisTemplate(String instanceName, RedisTemplate redisTemplate) {
        TEMPLATES.put(instanceName, redisTemplate);
    }

    public void putRedisConnectionFactory(String instanceName, RedisConnectionFactory connectionFactory) {
        CONNECTION_FACTORIES.put(instanceName, connectionFactory);
    }

}
