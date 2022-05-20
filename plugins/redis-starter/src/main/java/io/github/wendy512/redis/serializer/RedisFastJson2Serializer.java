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
