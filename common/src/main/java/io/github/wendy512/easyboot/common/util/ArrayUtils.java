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

package io.github.wendy512.easyboot.common.util;

import java.nio.*;
import java.nio.charset.StandardCharsets;

/**
 * 数组工具类
 * @author wendy512
 * @date 2022-05-20 21:28:56
 * @since 1.0.0
 */
public final class ArrayUtils {
    private ArrayUtils() {}

    /**
     * An empty immutable {@code Object} array.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    /**
     * An empty immutable {@code String} array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * An empty immutable {@code long} array.
     */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    /**
     * An empty immutable {@code Long} array.
     */
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    /**
     * An empty immutable {@code int} array.
     */
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    /**
     * An empty immutable {@code Integer} array.
     */
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    /**
     * An empty immutable {@code short} array.
     */
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    /**
     * An empty immutable {@code Short} array.
     */
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    /**
     * An empty immutable {@code byte} array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    /**
     * An empty immutable {@code Byte} array.
     */
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    /**
     * An empty immutable {@code double} array.
     */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    /**
     * An empty immutable {@code Double} array.
     */
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    /**
     * An empty immutable {@code float} array.
     */
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    /**
     * An empty immutable {@code Float} array.
     */
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    /**
     * An empty immutable {@code boolean} array.
     */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    /**
     * An empty immutable {@code Boolean} array.
     */
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    /**
     * An empty immutable {@code char} array.
     */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    /**
     * An empty immutable {@code Character} array.
     */
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    
    public static byte[] convertToByteArray(char[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        CharBuffer buffer = CharBuffer.allocate(input.length);
        buffer.put(input);
        buffer.flip();
        return StandardCharsets.UTF_8.encode(buffer).array();
    }

    public static byte[] convertToByteArray(double[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        
        DoubleBuffer doubleBuffer = DoubleBuffer.allocate(input.length);
        doubleBuffer.put(input);
        doubleBuffer.flip();

        return convertToByteArray(doubleBuffer, Double.BYTES);
    }

    public static byte[] convertToByteArray(float[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        
        FloatBuffer floatBuffer = FloatBuffer.allocate(input.length);
        floatBuffer.put(input);
        floatBuffer.flip();

        return convertToByteArray(floatBuffer, Float.BYTES);
    }

    public static byte[] convertToByteArray(int[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        
        IntBuffer intBuffer = IntBuffer.allocate(input.length);
        intBuffer.put(input);
        intBuffer.flip();

        return convertToByteArray(intBuffer, Integer.BYTES);
    }

    public static byte[] convertToByteArray(long[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        
        LongBuffer longBuffer = LongBuffer.allocate(input.length);
        longBuffer.put(input);
        longBuffer.flip();

        return convertToByteArray(longBuffer, Long.BYTES);
    }

    public static byte[] convertToByteArray(short[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        
        ShortBuffer shortBuffer = ShortBuffer.allocate(input.length);
        shortBuffer.put(input);
        shortBuffer.flip();

        return convertToByteArray(shortBuffer, Short.BYTES);
    }
    
    public static char[] convertToCharArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(input.length);
        byteBuffer.put(input);
        byteBuffer.flip();
        
        return StandardCharsets.UTF_8.decode(byteBuffer).array();
    }

    public static double[] convertToDoubleArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        double[] result = new double[input.length / Double.BYTES];
        doubleBuffer.get(result,0, result.length);
        return result;
    }

    public static float[] convertToFloatArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_FLOAT_ARRAY;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        float[] result = new float[input.length / Float.BYTES];
        floatBuffer.get(result,0, result.length);
        return result;
    }

    public static int[] convertToIntArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_INT_ARRAY;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        int[] result = new int[input.length / Integer.BYTES];
        intBuffer.get(result,0, result.length);
        return result;
    }

    public static long[] convertToLongArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_LONG_ARRAY;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        long[] result = new long[input.length / Long.BYTES];
        longBuffer.get(result,0, result.length);
        return result;
    }

    public static short[] convertToShortArray(byte[] input) {
        if (null == input || input.length == 0) {
            return EMPTY_SHORT_ARRAY;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        short[] result = new short[input.length / Short.BYTES];
        shortBuffer.get(result,0, result.length);
        return result;
    }

    private static byte[] convertToByteArray(Buffer buffer, int bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.remaining() * bytes);
        byteBuffer.order(ByteOrder.nativeOrder());
        buffer.mark();
        
        if (buffer instanceof DoubleBuffer) {
            byteBuffer.asDoubleBuffer().put((DoubleBuffer)buffer);
        } else if (buffer instanceof FloatBuffer) {
            byteBuffer.asFloatBuffer().put((FloatBuffer)buffer);
        } else if (buffer instanceof IntBuffer) {
            byteBuffer.asIntBuffer().put((IntBuffer)buffer);
        } else if (buffer instanceof LongBuffer) {
            byteBuffer.asLongBuffer().put((LongBuffer)buffer);
        } else {
            byteBuffer.asShortBuffer().put((ShortBuffer)buffer);
        }
        
        buffer.reset();
        byteBuffer.rewind();
        return byteBuffer.array();
    }

}
