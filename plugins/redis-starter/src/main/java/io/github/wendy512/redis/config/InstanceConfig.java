package io.github.wendy512.redis.config;

import java.util.List;

import lombok.Data;

/**
 * redis 实例配置
 * 
 * @author taowenwu
 * @date 2021-04-25 14:06:14:06
 * @since 1.0.0
 */
@Data
public class InstanceConfig {
    public InstanceConfig() {}

    public InstanceConfig(String host, int port, String password, long timeout) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
    }

    private String host = "localhost";
    private int port = 6379;
    private Integer database;
    private String password;
    private long timeout = 30 * 1000;
    private String keySerializer = "org.springframework.data.redis.serializer.StringRedisSerializer";
    private String valueSerializer = "org.springframework.data.redis.serializer.JdkSerializationRedisSerializerr";
    private String hashKeySerializer = "org.springframework.data.redis.serializer.StringRedisSerializer";
    private String hashValueSerializer = "org.springframework.data.redis.serializer.JdkSerializationRedisSerializer";
    private String master;
    /**
     * 哨兵模式
     */
    private boolean enableSentinel = false;

    private List<NodeConfig> sentinelNodes;

    /**
     * 集群模式
     */
    private boolean enableCluster = false;

    private List<NodeConfig> clusterNodes;

}
