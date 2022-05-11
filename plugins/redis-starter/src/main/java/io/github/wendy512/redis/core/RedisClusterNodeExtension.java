package io.github.wendy512.redis.core;

import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * redis cluster node 拓展
 * @author taowenwu
 * @date 2021-04-30 09:52:9:52
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class RedisClusterNodeExtension {
    private RedisClusterNode node;
    private RedisConnectionFactory connectionFactory;
    private RedisConnection connection;
}
