package io.github.wendy512.serialization;

import java.util.HashMap;
import java.util.Map;

import io.github.wendy512.serialization.hessian.HessianSerializer;
import io.github.wendy512.serialization.jdk.JdkSerializer;
import io.github.wendy512.serialization.json.JsonSerializer;
import io.github.wendy512.serialization.kryo.KryoSerializer;
import io.github.wendy512.serialization.protostuff.ProtoStuffSerializer;

/**
 * 序列化选择器
 * @author taowenwu
 * @date 2021-05-18 11:04:11:04
 * @since 1.0.0
 */
public final class SerializeSelector {
    private SerializeSelector() {}
    private static Map<SerializeType, Serializer> CACHE_SERIALIZER = new HashMap<>(5);
    
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
