package io.github.wendy512.common.serialization.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.github.wendy512.common.serialization.SerializationException;
import io.github.wendy512.common.serialization.Serializer;

/**
 * Kryo 序列化器，序列化类必须实现{@link java.io.Serializable}
 * @author taowenwu
 * @date 2021-05-18 11:25:11:25
 * @since 1.0.0
 */
public class KryoSerializer<T> implements Serializer<T> {

    private static final ThreadLocal<Kryo> KRYO_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 支持对象循环引用（否则会栈溢出），会导致性能些许下降 T_T
        kryo.setReferences(true);
        // 关闭序列化注册，会导致性能些许下降，但在分布式环境中，注册类生成ID不一致会导致错误
        kryo.setRegistrationRequired(false);
        // 设置类加载器为线程上下文类加载器（如果Processor来源于容器，必须使用容器的类加载器，否则妥妥的CNF）
        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        return kryo;
    });

    @Override
    public byte[] serialize(T t) throws SerializationException {
        Kryo kryo = KRYO_LOCAL.get();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] ret;

        try (Output opt = new Output(bos)) {
            kryo.writeObject(opt, t);
            opt.flush();
            ret = bos.toByteArray();
        } catch (Exception e) {
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
    public T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        Kryo kryo = KRYO_LOCAL.get();
        return kryo.readObject(new Input(bytes), clazz);
    }

}
