package io.github.wendy512.compress;

import java.io.IOException;

/**
 * 统一压缩接口
 * @author wendy512
 * @date 2022-05-20 17:43:55
 * @since 1.0.0
 */
public interface Compressor {

    byte[] compress(byte[] input) throws IOException;

    byte[] compress(char[] input) throws IOException;
    
    byte[] compress(double[] input) throws IOException;

    byte[] compress(float[] input) throws IOException;

    byte[] compress(int[] input) throws IOException;

    byte[] compress(long[] input) throws IOException;

    byte[] compress(short[] input) throws IOException;

    byte[] compress(String s) throws IOException;
}
