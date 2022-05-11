package io.github.wendy512.redis.serializer;

import io.github.wendy512.common.serialization.SerializeSelector;
import io.github.wendy512.common.serialization.SerializeType;
import io.github.wendy512.common.serialization.Serializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * redis protostuff 序列化
 * @author taowenwu
 * @date 2021-05-18 16:39:16:39
 * @since 1.0.0
 */
public class RedisProtoStuffSerializer<T> implements RedisSerializer<T> {
    private Serializer serializer = SerializeSelector.select(SerializeType.PROTOSTUFF);

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return serializer.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return (T) serializer.deserialize(bytes);
    }
}
