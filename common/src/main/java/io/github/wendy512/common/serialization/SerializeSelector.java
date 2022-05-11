package io.github.wendy512.common.serialization;

import java.util.HashMap;
import java.util.Map;

import io.github.wendy512.common.serialization.impl.*;

/**
 * 序列化选择器
 * @author taowenwu
 * @date 2021-05-18 11:04:11:04
 * @since 1.0.0
 */
public final class SerializeSelector {
    private SerializeSelector() {}
    private static Map<SerializeType, Serializer> CACHE_SERIALIZER = new HashMap<>();
    private volatile static SerializeSelector INSTANCE;
    
    static {
        CACHE_SERIALIZER.put(SerializeType.HESSIAN, new HessianSerializer());
        CACHE_SERIALIZER.put(SerializeType.JSON, new JsonSerializer());
        CACHE_SERIALIZER.put(SerializeType.KRYO, new KryoSerializer());
        CACHE_SERIALIZER.put(SerializeType.PROTOSTUFF, new ProtoStuffSerializer());
        CACHE_SERIALIZER.put(SerializeType.JDK, new JdkSerializer());
    }
    
    public static Serializer select(SerializeType serializeType) {
        return CACHE_SERIALIZER.get(serializeType);
    }
}
