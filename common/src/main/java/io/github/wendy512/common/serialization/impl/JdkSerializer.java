package io.github.wendy512.common.serialization.impl;

import java.io.*;

import io.github.wendy512.common.serialization.SerializationException;
import io.github.wendy512.common.serialization.Serializer;

/**
 * jdk序列化
 * 
 * @author taowenwu
 * @date 2021-05-18 14:27:14:27
 * @since 1.0.0
 */
public class JdkSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        byte[] ret;

        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            ret = bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                if (null != oos) oos.close();
                bos.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return ret;
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        T instance;

        try {
            ois = new ObjectInputStream(bis);
            instance = (T) ois.readObject();
        } catch (Exception e) {
            throw new SerializationException(e);
        } finally {
            try {
                if (null != ois) ois.close();
                bis.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return instance;
    }

}
