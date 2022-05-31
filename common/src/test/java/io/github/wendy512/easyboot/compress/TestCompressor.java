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

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试压缩
 * @author wendy512
 * @date 2022-05-21 13:25:22
 * @since 1.0.0
 */
public class TestCompressor {
    
    @Test
    public void testCompress() throws Exception {
        
        List<CompressObject> compressObjects = new ArrayList<>();
        
        for (int i = 0; i < 100000; i++) {
            CompressObject compressObject = new CompressObject();
            
            compressObject.setField1(RandomStringUtils.randomAlphabetic(10, 20));
            compressObject.setField2(RandomUtils.nextInt(100, 1000000));
            compressObject.setField3(RandomUtils.nextFloat(100, 1000000));
            compressObject.setField4(RandomUtils.nextDouble(100, 1000000));
            compressObject.setField5(RandomUtils.nextLong(100, 1000000));

            compressObjects.add(compressObject);
        }

        byte[] source = JSON.toJSONString(compressObjects).getBytes(StandardCharsets.UTF_8);
        System.out.println("source byte size: " + source.length);

        byte[] bytes = executeCompress(source, CompressType.BZIP2);
        executeUnCompress(bytes, CompressType.BZIP2);

        bytes = executeCompress(source, CompressType.DEFLATE);
        executeUnCompress(bytes, CompressType.DEFLATE);
        
        bytes = executeCompress(source, CompressType.GZIP);
        executeUnCompress(bytes, CompressType.GZIP);
        
        bytes = executeCompress(source, CompressType.FRAMEDLZ4);
        executeUnCompress(bytes, CompressType.FRAMEDLZ4);
        
        bytes = executeCompress(source, CompressType.LZMA);
        executeUnCompress(bytes, CompressType.LZMA);
        
        bytes = executeCompress(source, CompressType.SNAPPY);
        executeUnCompress(bytes, CompressType.SNAPPY);
        
        bytes = executeCompress(source, CompressType.XZ);
        executeUnCompress(bytes, CompressType.XZ);
        
        bytes = executeCompress(source, CompressType.ZSTD);
        executeUnCompress(bytes, CompressType.ZSTD);
        
    }

    private byte[] executeCompress(byte[] source, CompressType compressType) throws IOException {
        long start = System.currentTimeMillis();
        Compressor compressor = CompressFactory.create(compressType);
        byte[] bytes = compressor.compress(source);
        long end = System.currentTimeMillis();
        System.out.println(String.format("compress by %s, byte size: %s, usage time(ms): %s",compressType.getType(), bytes.length, (end - start)));
        return bytes;
    }

    private byte[] executeUnCompress(byte[] source, CompressType compressType) throws IOException {
        long start = System.currentTimeMillis();
        Compressor compressor = CompressFactory.create(compressType);
        byte[] bytes = compressor.unCompress(source);
        long end = System.currentTimeMillis();
        System.out.println(String.format("un compress by %s, byte size: %s, usage time(ms): %s",compressType.getType(), bytes.length, (end - start)));
        System.out.println("");
        return bytes;
    }
}
