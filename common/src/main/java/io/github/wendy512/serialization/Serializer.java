package io.github.wendy512.serialization;

/**
 * 序列化接口
 * @author taowenwu
 * @date 2021-05-18 11:11:11:11
 * @since 1.0.0
 */
public interface Serializer {

    <T> byte[] serialize(T t) throws SerializationException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException;

    <T> T deserialize(byte[] bytes) throws SerializationException;
}
