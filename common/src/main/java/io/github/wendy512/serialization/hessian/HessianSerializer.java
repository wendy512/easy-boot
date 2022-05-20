package io.github.wendy512.serialization.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;

/**
 * hessian 序列化
 * @author taowenwu
 * @date 2021-05-18 11:45:11:45
 * @since 1.0.0
 */
public class HessianSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        HessianOutput opt = null;
        byte[] ret;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            opt = new HessianOutput(os);
            opt.writeObject(t);
            opt.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                if (null != opt) opt.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        T instance;
        HessianInput input = null;

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            input = new HessianInput(is);
            if (null != clazz) {
                return (T) input.readObject(clazz);
            } else {
                return (T) input.readObject();
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            if (null != input) input.close();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        return deserialize(bytes, null);
    }

}
