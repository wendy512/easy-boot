package io.github.wendy512.redis.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * redis 模板配置
 * @author taowenwu
 * @date 2021-04-25 14:41:14:41
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "tom-framework.redis")
@Configuration
@Data
public class RedisTemplateConfig {
    private PoolConfig pool = new PoolConfig();
    private Map<String, InstanceConfig> instances;
    private InstanceConfig instance;

}
