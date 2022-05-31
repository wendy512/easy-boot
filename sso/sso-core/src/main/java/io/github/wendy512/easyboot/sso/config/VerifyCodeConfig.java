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

package io.github.wendy512.easyboot.sso.config;

import lombok.Data;

/**
 * 验证码配置
 * @author taowenwu
 * @date 2021-04-11 20:04:20:04
 * @since 1.0.0
 */
@Data
public class VerifyCodeConfig {

    /**
     * 是否启用验证码
     */
    private boolean enable = true;

    /**
     * 验证码类型，目前支持：
     * 1. simple 随机数字+字母验证码
     * 2. math 算术题验证码
     * 3. slide 滑动验证码
     * 4. piecing 拼图验证码
     */
    private String type = "simple";
}
