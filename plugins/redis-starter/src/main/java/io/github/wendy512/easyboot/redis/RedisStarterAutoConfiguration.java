/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.wendy512.easyboot.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import io.github.wendy512.easyboot.compress.CompressFactory;
import io.github.wendy512.easyboot.compress.CompressType;
import io.github.wendy512.easyboot.compress.Compressor;
import io.github.wendy512.easyboot.redis.config.CompressConfig;
import io.github.wendy512.easyboot.redis.config.InstanceConfig;
import io.github.wendy512.easyboot.redis.config.PoolConfig;
import io.github.wendy512.easyboot.redis.config.RedisTemplateConfig;
import io.github.wendy512.easyboot.redis.core.RedisApplicationContext;
import io.github.wendy512.easyboot.redis.core.RedisClusterSupportHolder;
import io.github.wendy512.easyboot.redis.core.extension.RedisTemplateClusterExtension;
import io.github.wendy512.easyboot.redis.serializer.RedisCompressSerializer;

/**
 * spring 自动装配
 * 
 * @author taowenwu
 * @date 2021-04-25 11:38:11:38
 * @since 1.0.0
 */
@ComponentScan("io.github.wendy512.easyboot.redis")
@Configuration
@EnableConfigurationProperties(RedisTemplateConfig.class)
public class RedisStarterAutoConfiguration {

    public static final String DEFAULT_INSTANCE_KEY = "default";

    private AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @ConditionalOnProperty(prefix = "easy-boot.redis", name = "enable", havingValue = "true", matchIfMissing = true)
    @Bean
    public RedisApplicationContext redisApplicationContext(RedisTemplateConfig config) throws Exception {
        RedisApplicationContext context = initialize(config);
        RedisUtils.setRedisApplicationContext(context);
        return context;
    }

    public RedisApplicationContext initialize(RedisTemplateConfig config) throws Exception {
        RedisApplicationContext context = new RedisApplicationContext();

        if (INITIALIZED.compareAndSet(false, true)) {
            validate(config);

            Map<String, InstanceConfig> instances = config.getInstances();
            if (null == instances) {
                instances = new HashMap<>(1);
                instances.put(DEFAULT_INSTANCE_KEY, config.getInstance());
            }

            Set<Map.Entry<String, InstanceConfig>> entries = instances.entrySet();
            for (Map.Entry<String, InstanceConfig> entry : entries) {

                InstanceConfig instance = entry.getValue();
                RedisMode mode;

                if (instance.isEnableCluster()) {
                    mode = RedisMode.CLUSTER;
                } else if (instance.isEnableSentinel()) {
                    mode = RedisMode.SENTINEL;
                } else {
                    mode = RedisMode.SINGLE;
                }

                RedisConnectionFactory connectionFactory =
                    createConnectionFactory(entry.getKey(), instance, mode, config.getPool());
                context.putRedisConnectionFactory(entry.getKey(), connectionFactory);

                RedisTemplateClusterExtension redisTemplateCluster =
                    createRedisTemplateBean(connectionFactory, entry.getKey(), instance, config.getPool());
                context.putRedisTemplate(entry.getKey(), redisTemplateCluster);
            }
        }

        return context;
    }

    private RedisTemplateClusterExtension createRedisTemplateBean(RedisConnectionFactory connectionFactory, String name,
        InstanceConfig instance, PoolConfig poolConfig) throws Exception {
        RedisTemplateClusterExtension redisTemplateCluster = new RedisTemplateClusterExtension();

        if (instance.isEnableCluster()) {
            RedisClusterSupportHolder clusterSupportHolder =
                new RedisClusterSupportHolder(connectionFactory, poolConfig);
            redisTemplateCluster.setClusterSupportHolder(clusterSupportHolder);
        }

        redisTemplateCluster.setConnectionFactory(connectionFactory);
        RedisSerializer keySerializer = (RedisSerializer)Class.forName(instance.getKeySerializer()).newInstance();
        RedisSerializer valueSerializer = (RedisSerializer)Class.forName(instance.getValueSerializer()).newInstance();
        RedisSerializer hashKeySerializer =
            (RedisSerializer)Class.forName(instance.getHashKeySerializer()).newInstance();
        RedisSerializer hashValueSerializer =
            (RedisSerializer)Class.forName(instance.getHashValueSerializer()).newInstance();

        CompressConfig compressConfig = instance.getCompressConfig();
        if (null != compressConfig && compressConfig.isEnable()) {

            Compressor compressor = CompressFactory.create(CompressType.valueOf(compressConfig.getType()));

            if (valueSerializer instanceof RedisCompressSerializer) {
                ((RedisCompressSerializer)valueSerializer).setCompressor(compressor);
            }

            if (valueSerializer instanceof RedisCompressSerializer) {
                ((RedisCompressSerializer)hashValueSerializer).setCompressor(compressor);
            }

            if (keySerializer instanceof RedisCompressSerializer) {
                ((RedisCompressSerializer)keySerializer).setCompressor(compressor);
            }

            if (hashKeySerializer instanceof RedisCompressSerializer) {
                ((RedisCompressSerializer)hashKeySerializer).setCompressor(compressor);
            }
        }

        redisTemplateCluster.setKeySerializer(keySerializer);
        redisTemplateCluster.setValueSerializer(valueSerializer);
        redisTemplateCluster.setHashKeySerializer(hashKeySerializer);
        redisTemplateCluster.setHashValueSerializer(hashValueSerializer);

        redisTemplateCluster.setPoolConfig(poolConfig);
        redisTemplateCluster.afterPropertiesSet();
        return redisTemplateCluster;
    }

    private RedisConnectionFactory createConnectionFactory(String name, InstanceConfig instance, RedisMode mode,
        PoolConfig poolConfig) {
        return RedisConnectionFactoryBuilder.builder().mode(mode).clientName(name).poolConfig(poolConfig)
            .enableNodeMapping(instance.isEnableNodeMapping()).instanceConfig(instance).build();
    }

    private void validate(RedisTemplateConfig config) {
        Assert.notNull(config, "redis 配置不能为空");
        if (null == config.getInstance()) {
            Assert.notNull(config.getInstances(), "redis.instances 不能为空");
        } else {
            Assert.notNull(config.getInstance(), "redis.instance 不能为空");
        }
    }

}
