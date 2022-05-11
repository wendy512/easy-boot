package io.github.wendy512.common.serialization.impl;


import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.common.serialization.SerializationException;
import io.github.wendy512.common.serialization.Serializer;

/**
 * json 序列化
 * @author taowenwu
 * @date 2021-05-18 11:16:11:16
 * @since 1.0.0
 */
public class JsonSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return JSON.toJSONString(t).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        return JSON.parseObject(bytes, clazz);
    }
}
