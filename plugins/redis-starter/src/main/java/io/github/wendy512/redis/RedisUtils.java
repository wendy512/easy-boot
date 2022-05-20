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

package io.github.wendy512.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.redis.core.RedisApplicationContext;


/**
 * redis 工具类
 * 
 * @author taowenwu
 * @date 2021-08-07 11:29:11:29
 * @since 1.0.0
 */
public class RedisUtils {
    private static RedisApplicationContext context;

    static void setRedisApplicationContext(RedisApplicationContext redisApplicationContext) {
        context = redisApplicationContext;
    }

    public static RedisTemplate getTemplate() {
        return context.getRedisTemplate();
    }

    public static RedisTemplate getTemplate(String template) {
        return context.getRedisTemplate(template);
    }

    public static <T> T getObject(Class<T> clazz, String key) {
        return getObject(clazz, RedisApplicationContext.DEFAULT_NAME, key);
    }

    public static <T> T getObject(Class<T> clazz, String template, String key) {
        String text = (String)context.getRedisTemplate(template).opsForValue().get(key);
        if (StringUtils.isNotEmpty(text)) {
            return JSON.parseObject(text, clazz);
        }
        return null;
    }

    public static <T> List<T> getArray(Class<T> clazz, String key) {
        return getArray(clazz, RedisApplicationContext.DEFAULT_NAME, key);
    }

    public static <T> List<T> getArray(Class<T> clazz, String template, String key) {
        String text = (String)context.getRedisTemplate(template).opsForValue().get(key);
        if (StringUtils.isNotEmpty(text)) {
            return JSON.parseArray(text, clazz);
        }
        return new ArrayList<>(0);
    }
}
