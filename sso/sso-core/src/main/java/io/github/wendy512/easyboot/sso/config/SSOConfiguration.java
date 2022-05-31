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

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * SSO server配置
 * @author taowenwu
 * @date 2021-04-05 18:33:18:33
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = SSOConfiguration.CONFIG_PREFIX)
@Data
public class SSOConfiguration {
    
    public static final String CONFIG_PREFIX = "easy-boot.sso";
    
    /**
     * token 存储方式，默认redis，支持jdbc方式，但建议使用redis
     */
    private String tokenStore = "redis";

    /**
     * 指定resourceId，要和oauth_client_details表中resource_ids保持一致
     */
    private String resourceId = "sso-resource";

    /**
     * 是否启用csrf，默认不启用
     */
    private boolean enableCsrf = false;

    /**
     * 验证码配置
     */
    private VerifyCodeConfig verifyCode = new VerifyCodeConfig();

    /**
     * 需要排除校验的url
     */
    private ExcludeUrl exclude;

}
