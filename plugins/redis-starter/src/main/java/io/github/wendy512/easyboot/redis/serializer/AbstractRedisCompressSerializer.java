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

import io.github.wendy512.easyboot.common.util.ArrayUtils;
import io.github.wendy512.easyboot.compress.Compressor;
import io.github.wendy512.easyboot.serialization.Serializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * 抽象序列类
 * @author wendy512
 * @date 2022-05-21 11:37:59
 * @since 1.0.0
 */
public abstract class AbstractRedisCompressSerializer<T> implements RedisCompressSerializer<T> {

    private Compressor compressor;
    
    @Override
    public byte[] serialize(T t) throws SerializationException {
        
        byte[] data = SerializeUtil.serialize(getSerializer(), t);
        
        if (null != compressor) {
            try {
                return compressor.compress(data);
            } catch (IOException e) {
                throw new SerializationException("compress error", e);
            }
        }
        return data;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
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
        
        return SerializeUtil.deserialize(getSerializer(), source);
    }

    @Override
    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    @Override
    public Compressor getCompressor() {
        return this.compressor;
    }
    
    public abstract Serializer getSerializer();
}
