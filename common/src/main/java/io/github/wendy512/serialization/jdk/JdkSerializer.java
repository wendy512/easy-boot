package io.github.wendy512.serialization.jdk;

import java.io.*;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;

/**
 * jdk序列化
 * 
 * @author taowenwu
 * @date 2021-05-18 14:27:14:27
 * @since 1.0.0
 */
public class JdkSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] ret;

        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(t);
            ret = bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return ret;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        T instance;

        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            instance = (T) ois.readObject();
        } catch (Exception e) {
            throw new SerializationException(e);
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return instance;
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        return deserialize(bytes, null);
    }

}
