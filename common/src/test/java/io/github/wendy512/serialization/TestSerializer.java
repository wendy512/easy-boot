package io.github.wendy512.serialization;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试序列化
 * 
 * @author wendy512
 * @date 2022-05-11 18:15:18:15
 * @since 1.0.0
 */
public class TestSerializer {
    private static final int SERIALIZE_NUMS = 1000000;
    private static final Map map = new HashMap();
    
    static {
        for (int i = 0; i < SERIALIZE_NUMS; i++) {
            map.put("key" + i, RandomStringUtils.randomAlphabetic(10, 20));
        }
    }
    
    @Test
    public void testHessian() {
        doSerialize(SerializeType.HESSIAN);
    }

    @Test
    public void testJdk() {
        doSerialize(SerializeType.JDK);
    }

    @Test
    public void testProtostuff() {
        doSerialize(SerializeType.PROTOSTUFF);
    }

    @Test
    public void testKryo() {
        doSerialize(SerializeType.KRYO);
    }

    @Test
    public void testJson() {
        doSerialize(SerializeType.JSON);
    }
    
    @Test
    public void testAll() {
        testHessian();
        testJdk();
        testProtostuff();
        testKryo();
        testJson();
    }

    private void doSerialize(SerializeType serializeType) {
        Serializer serializer = SerializeSelector.select(serializeType);
        long start = System.currentTimeMillis();
        byte[] b = serializer.serialize(map);
        serializer.deserialize(b, HashMap.class);
        long end = System.currentTimeMillis();
        System.out.println(serializeType.getName() + " serialize usage time: " + (end - start) + "ms");
    }


}