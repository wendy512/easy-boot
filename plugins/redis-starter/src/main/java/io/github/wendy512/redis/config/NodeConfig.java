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

package io.github.wendy512.redis.config;

import lombok.Data;

/**
 * 节点配置
 * @author taowenwu
 * @date 2021-04-25 14:34:14:34
 * @since 1.0.0
 */
@Data
public class NodeConfig {
    private String node;
    /**
     * 容器中的节点IP和Port，为了解决获取集群节点时IP隐射问题
     */
    private String containerNode;
}
