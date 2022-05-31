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

package io.github.wendy512.easyboot.redis.serializer.string;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.github.wendy512.easyboot.common.util.ArrayUtils;
import io.github.wendy512.easyboot.compress.Compressor;
import io.github.wendy512.easyboot.redis.serializer.RedisCompressSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * redis string序列化器支持压缩
 * @author wendy512
 * @date 2022-05-23 10:58:40
 * @since 1.0.0
 */
public class StringExtensionRedisSerializer<T> implements RedisCompressSerializer<String> {
    
    private Compressor compressor;
    
    @Override
    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    @Override
    public Compressor getCompressor() {
        return this.compressor;
    }

    @Override
    public byte[] serialize(String s) throws SerializationException {
        
        if (s == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        byte[] source = s.getBytes(StandardCharsets.UTF_8);
        
        if (null != compressor) {
            
            try {
                return compressor.compress(source);
            } catch (IOException e) {
                throw new SerializationException("compress error", e);
            }
        }
        return source;
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        
        byte[] source = ArrayUtils.EMPTY_BYTE_ARRAY;
        if (null != compressor) {
            try {
                source = compressor.unCompress(bytes);
            } catch (IOException e) {
                throw new SerializationException("compress error", e);
            }
        } else {
            source = bytes;
        }
        return new String(source, StandardCharsets.UTF_8);
    }
}
