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

package io.github.wendy512.easyboot.serialization;

import java.util.HashMap;
import java.util.Map;

import io.github.wendy512.easyboot.serialization.protostuff.ProtoStuffSerializer;
import io.github.wendy512.easyboot.serialization.hessian.HessianSerializer;
import io.github.wendy512.easyboot.serialization.jdk.JdkSerializer;
import io.github.wendy512.easyboot.serialization.json.JsonSerializer;
import io.github.wendy512.easyboot.serialization.kryo.KryoSerializer;

/**
 * 序列化选择器
 * @author taowenwu
 * @date 2021-05-18 11:04:11:04
 * @since 1.0.0
 */
public final class SerializeSelector {
    private SerializeSelector() {}
    private static Map<SerializeType, Serializer> CACHE_SERIALIZER = new HashMap<>(5);
    
    static {
        CACHE_SERIALIZER.put(SerializeType.HESSIAN, new HessianSerializer());
        CACHE_SERIALIZER.put(SerializeType.JSON, new JsonSerializer());
        CACHE_SERIALIZER.put(SerializeType.KRYO, new KryoSerializer());
        CACHE_SERIALIZER.put(SerializeType.PROTOSTUFF, new ProtoStuffSerializer());
        CACHE_SERIALIZER.put(SerializeType.JDK, new JdkSerializer());
    }
    
    public static Serializer select(SerializeType serializeType) {
        return CACHE_SERIALIZER.get(serializeType);
    }
}
