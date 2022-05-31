/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.wendy512.easyboot.redis.serializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.github.wendy512.easyboot.common.util.ArrayUtils;
import io.github.wendy512.easyboot.serialization.Serializer;

/**
 *
 * 序列化工具
 * @author wendy512
 * @date 2022-05-18 17:53:38
 * @since 1.0.0
 */
public final class SerializeUtil {
    private SerializeUtil() {}
    
    public static <T> byte[] serialize(Serializer serializer, T t) {
        if (null == t) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return serializer.serialize(t);
    }

    public static  <T> T deserialize(Serializer serializer, byte[] source) {
        if (null == source) {
            return null;
        }

        SerializeUtil.Tuple2<String, byte[]> tuple2 = subClassName(source);
        Class<?> clazz = null;
        try {
            clazz = Class.forName(tuple2.t1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (T)serializer.deserialize(tuple2.t2, clazz);
    }
    
    public static byte[] appendClassName(Object t, byte[] source) {
        String className = t.getClass().getName();
        byte[] classNameBytes = className.getBytes(StandardCharsets.UTF_8);
        int classNameBytesLen = classNameBytes.length;
        byte[] header = intToByteArray(classNameBytesLen);

        int headerLen = header.length;
        int originalBytesLen = source.length;
        byte[] data = new byte[headerLen + classNameBytesLen + originalBytesLen];
        System.arraycopy(header, 0, data, 0, headerLen);
        System.arraycopy(classNameBytes, 0, data, headerLen, classNameBytesLen);
        System.arraycopy(source, 0, data, headerLen + classNameBytesLen, originalBytesLen);
        return data;
    }

    public static Tuple2<String,byte[]> subClassName(byte[] source) {
        byte[] header = Arrays.copyOf(source, Integer.BYTES);
        int classNameLength = byteArrayToInt(header);
        byte[] classNameBytes = Arrays.copyOfRange(source, Integer.BYTES, Integer.BYTES + classNameLength);
        String className = new String(classNameBytes, StandardCharsets.UTF_8);
        byte[] data = Arrays.copyOfRange(source, Integer.BYTES + classNameLength, source.length);
        return new Tuple2<>(className, data);
    }


    public static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value & 0xFF);
        byteArray[1] = (byte)(value >> 8 & 0xFF);
        byteArray[2] = (byte)(value >> 16 & 0xFF);
        byteArray[3] = (byte)(value >> 24 & 0xFF);
        return byteArray;
    }

    public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != 4) {
            return 0;
        }
        int value = byteArray[0] & 0xFF;
        value |= byteArray[1] << 8;
        value |= byteArray[2] << 16;
        value |= byteArray[3] << 24;
        return value;
    }
    
    public static class Tuple2<T1,T2> {
        public final T1 t1;
        public final T2 t2;

        public Tuple2(T1 t1, T2 t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}
