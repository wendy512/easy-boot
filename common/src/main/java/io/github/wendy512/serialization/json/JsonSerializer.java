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

package io.github.wendy512.serialization.json;


import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;

/**
 * json 序列化
 * @author taowenwu
 * @date 2021-05-18 11:16:11:16
 * @since 1.0.0
 */
public class JsonSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        return JSON.toJSONString(t).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        return JSON.parseObject(bytes, clazz);
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        return (T) JSON.parseObject(bytes);
    }
}
