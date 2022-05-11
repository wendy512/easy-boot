package io.github.wendy512.common.serialization;

import java.lang.reflect.ParameterizedType;

/**
 * 序列化接口
 * @author taowenwu
 * @date 2021-05-18 11:11:11:11
 * @since 1.0.0
 */
public interface Serializer<T> {

    byte[] serialize(T t) throws SerializationException;

    default T deserialize(byte[] bytes) throws SerializationException {
        Class<T> clazz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return deserialize(bytes, clazz);
    };

    T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException;

}
