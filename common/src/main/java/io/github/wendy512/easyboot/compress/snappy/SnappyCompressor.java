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

package io.github.wendy512.easyboot.compress.snappy;

import io.github.wendy512.easyboot.compress.Compressor;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Snappy 压缩算法
 * @author wendy512
 * @date 2022-05-20 17:51:14
 * @since 1.0.0
 */
public class SnappyCompressor implements Compressor {
    
    @Override
    public byte[] compress(byte[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(char[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(double[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(float[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(int[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(long[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(short[] input) throws IOException {
        return Snappy.compress(input);
    }

    @Override
    public byte[] compress(String s) throws IOException {
        return Snappy.compress(s);
    }

    @Override
    public byte[] unCompress(byte[] input) throws IOException {
        return Snappy.uncompress(input);
    }

    @Override
    public char[] unCompressCharArray(byte[] input) throws IOException {
        return Snappy.uncompressCharArray(input);
    }

    @Override
    public double[] unCompressDoubleArray(byte[] input) throws IOException {
        return Snappy.uncompressDoubleArray(input);
    }

    @Override
    public float[] unCompressFloatArray(byte[] input) throws IOException {
        return Snappy.uncompressFloatArray(input);
    }

    @Override
    public int[] unCompressIntArray(byte[] input) throws IOException {
        return Snappy.uncompressIntArray(input);
    }

    @Override
    public long[] unCompressLongArray(byte[] input) throws IOException {
        return Snappy.uncompressLongArray(input);
    }

    @Override
    public short[] unCompressShortArray(byte[] input) throws IOException {
        return Snappy.uncompressShortArray(input);
    }

    @Override
    public String unCompressString(byte[] input) throws IOException {
        return Snappy.uncompressString(input);
    }
}
