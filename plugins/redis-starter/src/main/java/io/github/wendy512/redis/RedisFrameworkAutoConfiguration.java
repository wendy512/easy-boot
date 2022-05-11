package io.github.wendy512.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import io.github.wendy512.redis.config.InstanceConfig;
import io.github.wendy512.redis.config.RedisTemplateConfig;
import io.github.wendy512.redis.core.RedisApplicationContext;
import io.github.wendy512.redis.core.RedisClusterSupportHolder;
import io.github.wendy512.redis.core.RedisTemplateClusterExtension;


/**
 * spring 自动装配
 * 
 * @author taowenwu
 * @date 2021-04-25 11:38:11:38
 * @since 1.0.0
 */
@ComponentScan("io.github.wendy512.redis")
@Configuration
public class RedisFrameworkAutoConfiguration {
    private static AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    public static final String DEFAULT_INSTANCE_KEY = "default";
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private RedisTemplateConfig redisTemplateConfig;

    @Bean
    public RedisApplicationContext redisApplicationContext() throws Exception {
        RedisApplicationContext context = initialize();
        RedisUtils.setRedisApplicationContext(context);
        return context;
    }

    public RedisApplicationContext initialize() throws Exception {
        RedisApplicationContext context = new RedisApplicationContext();

        if (INITIALIZED.compareAndSet(false, true)) {
            validate();

            Map<String, InstanceConfig> instances = redisTemplateConfig.getInstances();
            if (null == instances) {
                instances = new HashMap<>(1);
                instances.put(DEFAULT_INSTANCE_KEY, redisTemplateConfig.getInstance());
            }
            Set<Map.Entry<String, InstanceConfig>> entries = instances.entrySet();
            for (Map.Entry<String, InstanceConfig> e : entries) {
                InstanceConfig instance = e.getValue();
                RedisMode mode;
                if (instance.isEnableCluster()) {
                    mode = RedisMode.CLUSTER;
                } else if (instance.isEnableSentinel()) {
                    mode = RedisMode.SENTINEL;
                } else {
                    mode = RedisMode.SINGLE;
                }
                RedisConnectionFactory connectionFactory = createConnectionFactory(e.getKey(), instance, mode);
                context.putRedisConnectionFactory(e.getKey(), connectionFactory);

                RedisTemplateClusterExtension redisTemplateCluster = createRedisTemplateBean(connectionFactory, e.getKey(), instance);
                context.putRedisTemplate(e.getKey(), redisTemplateCluster);
            }
        }

        return context;
    }

    private RedisTemplateClusterExtension createRedisTemplateBean(RedisConnectionFactory connectionFactory, String name, InstanceConfig instance) throws Exception {
        RedisTemplateClusterExtension redisTemplateCluster = new RedisTemplateClusterExtension();
        if (instance.isEnableCluster()) {
            RedisClusterSupportHolder clusterSupportHolder = new RedisClusterSupportHolder(connectionFactory, redisTemplateConfig.getPool());
            redisTemplateCluster.setClusterSupportHolder(clusterSupportHolder);
        }
        redisTemplateCluster.setConnectionFactory(connectionFactory);
        redisTemplateCluster.setKeySerializer((RedisSerializer)Class.forName(instance.getKeySerializer()).newInstance());
        redisTemplateCluster.setValueSerializer((RedisSerializer)Class.forName(instance.getValueSerializer()).newInstance());
        redisTemplateCluster.setHashKeySerializer((RedisSerializer)Class.forName(instance.getHashKeySerializer()).newInstance());
        redisTemplateCluster.setHashValueSerializer((RedisSerializer)Class.forName(instance.getHashValueSerializer()).newInstance());
        redisTemplateCluster.setPoolConfig(redisTemplateConfig.getPool());
        redisTemplateCluster.afterPropertiesSet();
        return redisTemplateCluster;
    }


    private RedisConnectionFactory createConnectionFactory(String name, InstanceConfig instance, RedisMode mode) {
        return RedisConnectionFactoryBuilder.builder().mode(mode).clientName(name)
                .poolConfig(redisTemplateConfig.getPool()).instanceConfig(instance).build();
    }

    private void validate() {
        Assert.notNull(redisTemplateConfig, "redis 配置不能为空");
        if (null == redisTemplateConfig.getInstance()) {
            Assert.notNull(redisTemplateConfig.getInstances(), "redis.instances 不能为空");
        } else {
            Assert.notNull(redisTemplateConfig.getInstance(), "redis.instance 不能为空");
        }
    }

    private void registerBean(String beanName, Class<?> clazz, Object instance) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        definitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        definitionBuilder.setLazyInit(Boolean.FALSE);
        this.beanFactory.registerBeanDefinition(beanName, definitionBuilder.getRawBeanDefinition());
    }

}
