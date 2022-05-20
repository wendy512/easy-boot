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
