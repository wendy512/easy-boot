/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.wendy512.easyboot.compress;

import io.github.wendy512.easyboot.compress.bzip2.BZip2Compressor;
import io.github.wendy512.easyboot.compress.deflate.DeflateCompressor;
import io.github.wendy512.easyboot.compress.gzip.GZIPCompressor;
import io.github.wendy512.easyboot.compress.lz4.FramedLZ4Compressor;
import io.github.wendy512.easyboot.compress.lzma.LZMACompressor;
import io.github.wendy512.easyboot.compress.snappy.SnappyCompressor;
import io.github.wendy512.easyboot.compress.xz.XZCompressor;
import io.github.wendy512.easyboot.compress.zstd.ZstdCompressor;

/**
 * 压缩算法工厂
 * 
 * @author wendy512
 * @date 2022-05-21 11:13:24
 * @since 1.0.0
 */
public final class CompressFactory {
    private CompressFactory() {}

    public static Compressor create(CompressType compressType) {
        switch (compressType) {
            case BZIP2:
                return new BZip2Compressor();
            case DEFLATE:
                return new DeflateCompressor();
            case GZIP:
                return new GZIPCompressor();
            case FRAMEDLZ4:
                return new FramedLZ4Compressor();
            case LZMA:
                return new LZMACompressor();
            case SNAPPY:
                return new SnappyCompressor();
            case XZ:
                return new XZCompressor();
            case ZSTD:
                return new ZstdCompressor();
            default:
               throw new IllegalArgumentException("unknown compressType");
        }

    }
}
