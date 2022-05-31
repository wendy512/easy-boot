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

import io.github.wendy512.easyboot.compress.Compressor;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis 带压缩序列化器
 * @author wendy512
 * @date 2022-05-21 11:30:27
 * @since 1.0.0
 */
public interface RedisCompressSerializer<T> extends RedisSerializer<T> {
    
    void setCompressor(Compressor compressor);

    Compressor getCompressor();
}
