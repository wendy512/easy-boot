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

package io.github.wendy512.easyboot.redis.config;

import lombok.Data;

/**
 * 数据压缩配置
 * @author wendy512
 * @date 2022-05-21 13:21:56
 * @since 1.0.0
 */
@Data
public class CompressConfig {
    /**
     * 是否启用数据压缩
     */
    private boolean enable = false;
    /**
     * 压缩算法类型，目前支持：bizp2、deflate、gzip、framedlz4、lzma、snappy、xz、zstd
     */
    private String type;
}
