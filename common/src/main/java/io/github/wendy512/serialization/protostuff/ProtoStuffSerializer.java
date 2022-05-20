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

package io.github.wendy512.serialization.protostuff;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * protostuff 序列化
 * @author taowenwu
 * @date 2021-05-18 13:52:13:52
 * @since 1.0.0
 */
public class ProtoStuffSerializer implements Serializer {

    //将数据封装
    private static final Set<Class<?>> WRAPPER_SET = new HashSet<>();
    //包装类的Class对象
    private static final Class<SerializeDeserializeWrapper> WRAPPER_CLASS = SerializeDeserializeWrapper.class;
    //安全缓存区，class对象和Schema对象
    private static final Map<Class<?>, Schema<?>> CACHE_SCHEMA = new ConcurrentHashMap<>();
    //包装类的Schema对象
    private static final Schema<SerializeDeserializeWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(WRAPPER_CLASS);

    static {
        WRAPPER_SET.add(List.class);
        WRAPPER_SET.add(ArrayList.class);
        WRAPPER_SET.add(CopyOnWriteArrayList.class);
        WRAPPER_SET.add(LinkedList.class);
        WRAPPER_SET.add(Stack.class);
        WRAPPER_SET.add(Vector.class);
        WRAPPER_SET.add(Map.class);
        WRAPPER_SET.add(HashMap.class);
        WRAPPER_SET.add(TreeMap.class);
        WRAPPER_SET.add(LinkedHashMap.class);
        WRAPPER_SET.add(Hashtable.class);
        WRAPPER_SET.add(SortedMap.class);
    }

    //注册需要使用包装类进行序列化的Class对象
    public void registerWrapperClass(Class clazz) {
        WRAPPER_SET.add(clazz);
    }

    //获取序列化对象类型的schema
    private <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) CACHE_SCHEMA.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            CACHE_SCHEMA.put(clazz, schema);
        }
        return schema;
    }

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        //获取序列化对象
        Class<T> clazz = (Class<T>) t.getClass();
        //设置缓数组缓冲区
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Object serializerObj = t;       //获取序列化对象
        Schema schema = WRAPPER_SCHEMA;   //获取Schema对象
        //包装class对象
        if (WRAPPER_SET.contains(clazz)) {
            //外部类是否可以使用静态内部类的成员？【外部类使用内部类的成员，需要新建内部类实例。】
            serializerObj = SerializeDeserializeWrapper.<T>builder(t);//将class对象进行包装
        } else {
            //将class对象和schema对象保存到hashMap中
            schema = getSchema(clazz);  //获取Schema对象
        }
        try {
            //将对象转换为字节流
            return ProtostuffIOUtil.toByteArray(serializerObj, schema, buffer);
        } catch (Exception e) {
            throw new SerializationException(e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        //判断是否是不可序列化对象，若是不能序列化对象，将对象进行包装
        if (WRAPPER_SET.contains(clazz)) {
            SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<>();
            ProtostuffIOUtil.mergeFrom(bytes, wrapper, WRAPPER_SCHEMA);
            T data = wrapper.getData();
            return data;
        } else {
            Schema<T> schema = getSchema(clazz);
            T instance = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
            return instance;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        throw new UnsupportedOperationException();
    }

    //静态内部类
    public static class SerializeDeserializeWrapper<T> {
        private T data;

        public static <T> SerializeDeserializeWrapper<T> builder(T data) {
            SerializeDeserializeWrapper<T> wrapper = new SerializeDeserializeWrapper<T>();
            wrapper.setData(data);
            return wrapper;
        }

        public void setData(T data) {
            this.data = data;
        }
        public T getData() {
            return data;
        }
    }
}
