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

package io.github.wendy512.redis.core;

import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * redis cluster node 拓展
 * @author taowenwu
 * @date 2021-04-30 09:52:9:52
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class RedisClusterNodeExtension {
    private RedisClusterNode node;
    private RedisConnectionFactory connectionFactory;
    private RedisConnection connection;
}
