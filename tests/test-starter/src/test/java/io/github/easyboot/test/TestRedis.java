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

package io.github.easyboot.test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.redis.RedisUtils;
import io.github.wendy512.redis.core.RedisApplicationContext;
import io.github.wendy512.redis.serializer.RedisFastJson2Serializer;
import io.github.wendy512.redis.serializer.RedisHessianSerializer;
import io.github.wendy512.redis.serializer.RedisKryoSerializer;
import io.github.wendy512.redis.serializer.RedisProtoStuffSerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试redis
 * @author wendy512
 * @date 2022-05-16 13:43:19
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class TestRedis {
    
    @Autowired
    private RedisApplicationContext redisApplicationContext;
    
    @Test
    public void testSingle() {
        doTestRedis("single");
    }

    @Test
    public void testCluster() {
        doTestRedis("cluster");
    }

    @Test
    public void testSentinel() {
        doTestRedis("sentinel");
    }

    private void doTestRedis(String templateName) {
        RedisTemplate redisTemplate = RedisUtils.getTemplate(templateName);
        Map map = new HashMap();
        for (int i = 0; i < 100000; i++) {
            map.put("key" + i, RandomStringUtils.randomAlphabetic(10, 20));
        }

        String jsonKey = "test-json-" + templateName;
        String hessianKey = "hessian-json" + templateName;
        String kryoKey = "kryo-json" + templateName;
        String protostuffKey = "protostuff-json" + templateName;

        long start = System.currentTimeMillis();
        //json
        redisTemplate.setValueSerializer(new RedisFastJson2Serializer<>());
        redisTemplate.opsForValue().set(jsonKey, map);
        Map map2 = (Map) redisTemplate.opsForValue().get(jsonKey);
        long end = System.currentTimeMillis();
        System.out.println(String.format("set and read by RedisFastJson2Serializer, usage time(ms): %s", (end - start)));

        start = System.currentTimeMillis();
        //hessian
        redisTemplate.setValueSerializer(new RedisHessianSerializer<>());
        redisTemplate.opsForValue().set(hessianKey, map);
        map2 = (Map) redisTemplate.opsForValue().get(hessianKey);
        end = System.currentTimeMillis();
        System.out.println(String.format("set and read by RedisHessianSerializer, usage time(ms): %s", (end - start)));

        //kryo
        start = System.currentTimeMillis();
        redisTemplate.setValueSerializer(new RedisKryoSerializer<>());
        redisTemplate.opsForValue().set(kryoKey, map);
        map2 = (Map) redisTemplate.opsForValue().get(kryoKey);
        end = System.currentTimeMillis();
        System.out.println(String.format("set and read by RedisKryoSerializer, usage time(ms): %s", (end - start)));

        //protostuff
        start = System.currentTimeMillis();
        redisTemplate.setValueSerializer(new RedisProtoStuffSerializer<>());
        redisTemplate.opsForValue().set(protostuffKey, map);
        map2 = (Map) redisTemplate.opsForValue().get(protostuffKey);
        end = System.currentTimeMillis();
        System.out.println(String.format("set and read by RedisProtoStuffSerializer, usage time(ms): %s", (end - start)));
        
        //测试pipeline
        String pipKey = "test-json-pip-" + templateName;
        redisTemplate.executePipelined((RedisCallback) session -> {
            return session.set(pipKey.getBytes(StandardCharsets.UTF_8), JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        });
    }
}
