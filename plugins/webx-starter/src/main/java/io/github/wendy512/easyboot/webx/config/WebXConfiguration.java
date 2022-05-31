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

package io.github.wendy512.easyboot.webx.config;

import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * wex 配置
 * 
 * @author wendy512
 * @date 2022-05-23 16:35:06
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "easy-boot.webx")
@Data
@Accessors(chain = true)
public class WebXConfiguration {
    
    private PrintParamConfiguration print = new PrintParamConfiguration();

    /**
     * 打印参数配置
     */
    @Data
    public static class PrintParamConfiguration {
        private boolean enablePrintRequest = false;
        private boolean enablePrintResponse = false;
    }
}
