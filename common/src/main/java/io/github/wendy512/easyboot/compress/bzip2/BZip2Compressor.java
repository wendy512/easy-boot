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

package io.github.wendy512.easyboot.compress.bzip2;

import java.io.*;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import io.github.wendy512.easyboot.compress.AbstractCompressor;

/**
 * bzip2 压缩算法
 * @author wendy512
 * @date 2022-05-21 10:43:53
 * @since 1.0.0
 */
public class BZip2Compressor extends AbstractCompressor {


    @Override
    protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException {
        return new BZip2CompressorOutputStream(out);
    }

    @Override
    protected InputStream createInputStream(ByteArrayInputStream in) throws IOException {
        return new BZip2CompressorInputStream(in);
    }
}
