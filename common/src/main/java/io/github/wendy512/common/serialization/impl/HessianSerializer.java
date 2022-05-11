package io.github.wendy512.common.serialization.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.HessianOutput;

import io.github.wendy512.common.serialization.SerializationException;
import io.github.wendy512.common.serialization.Serializer;

/**
 * hessian 序列化
 * @author taowenwu
 * @date 2021-05-18 11:45:11:45
 * @since 1.0.0
 */
public class HessianSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        ByteArrayOutputStream bytesOs = new ByteArrayOutputStream();
        HessianOutput opt = new HessianOutput(bytesOs);
        byte[] ret;

        try {
            opt.writeObject(t);
            opt.flush();
            ret = bytesOs.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                opt.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
            IOUtils.closeQuietly(bytesOs);
        }

        return ret;
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bis);
        T instance;

        try {
            instance = (T) input.readObject();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                input.close();
                bis.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return instance;
    }

}
