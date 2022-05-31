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

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * redis 模板配置
 * @author taowenwu
 * @date 2021-04-25 14:41:14:41
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "easy-boot.redis")
@Data
@Accessors(chain = true)
public class RedisTemplateConfig {
    private PoolConfig pool = new PoolConfig();
    private Map<String, InstanceConfig> instances;
    private InstanceConfig instance;

}
