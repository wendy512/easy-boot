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

package io.github.wendy512.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import io.github.wendy512.serialization.SerializeSelector;
import io.github.wendy512.serialization.SerializeType;
import io.github.wendy512.serialization.Serializer;

/**
 * redis fastjson2 序列化
 * 
 * @author taowenwu
 * @date 2021-05-18 16:39:16:39
 * @since 1.0.0
 */
public class RedisFastJson2Serializer<T> implements RedisSerializer<T> {
    private Serializer serializer = SerializeSelector.select(SerializeType.JSON);

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return SerializeUtil.serialize(serializer, t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return SerializeUtil.deserialize(serializer, bytes);
    }
}
