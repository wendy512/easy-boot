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

package io.github.wendy512.easyboot.compress;

import java.io.*;
import java.nio.charset.StandardCharsets;

import io.github.wendy512.easyboot.common.util.ArrayUtils;
import org.apache.commons.io.IOUtils;

/**
 * 抽象压缩类，主要基于Stream的操作
 * @author wendy512
 * @date 2022-05-21 10:45:50
 * @since 1.0.0
 */
public abstract class AbstractCompressor implements Compressor {

    public byte[] compress(byte[] input) throws IOException {
        if (null == input || input.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(CACHE_SIZE);
        try (OutputStream stream = createOutputStream(out)){
            stream.write(input);
        }
        byte[] output = out.toByteArray();
        out.close();
        return output;
    }
    
    @Override
    public byte[] compress(char[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(double[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(float[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(int[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(long[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(short[] input) throws IOException {
        return compress(ArrayUtils.convertToByteArray(input));
    }

    @Override
    public byte[] compress(String s) throws IOException {
        return compress(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] unCompress(byte[] input) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        byte[] ret = ArrayUtils.EMPTY_BYTE_ARRAY;

        try (InputStream stream = createInputStream(in)){
            ret = IOUtils.toByteArray(stream);
        }

        in.close();
        return ret;
    }

    @Override
    public char[] unCompressCharArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToCharArray(ret);
    }

    @Override
    public double[] unCompressDoubleArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToDoubleArray(ret);
    }

    @Override
    public float[] unCompressFloatArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToFloatArray(ret);
    }

    @Override
    public int[] unCompressIntArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToIntArray(ret);
    }

    @Override
    public long[] unCompressLongArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToLongArray(ret);
    }

    @Override
    public short[] unCompressShortArray(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return ArrayUtils.convertToShortArray(ret);
    }

    @Override
    public String unCompressString(byte[] input) throws IOException {
        byte[] ret = unCompress(input);
        return new String(ret, StandardCharsets.UTF_8);
    }

    protected abstract OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException;

    protected abstract InputStream createInputStream(ByteArrayInputStream in) throws IOException;
}
