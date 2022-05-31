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

package io.github.wendy512.easyboot.sso.constants;

/**
 * 常量类
 * @author taowenwu
 * @date 2021-04-25 09:13:9:13
 * @since 1.0.0
 */
public final class Constants {
    
    private Constants() {}
    
    public static final String SESSION_KEY = "easy-boot.session";
    
    public static final String AUTHENTICATIONMANAGER_BEAN_ID = "authenticationManagerBean";

    public static final String USER_DETAIL_SERVICE_BEAN_ID = "userDetailsServiceImpl";

    public static final String SSOTOKENSERVICES_BEAN_ID = "ssoTokenServices";

    public static final String JWTACCESSTOKENCONVERTER_BEAN_ID = "jwtAccessTokenConverter";

    public static final String REDISAUTHENTICATIONCODESERVICES_BEAN_ID = "redisAuthenticationCodeServices";

    public static final String SSOTOKENSTORE_BEAN_ID = "ssoTokenStore";

    public static final String TOKEN_STORE_JDBC = "jdbc";
    public static final String TOKEN_STORE_REDIS = "redis";
    public static final String TOKEN_STORE_JWT = "jwt";

    public static final String APPLICATION_JSON = "application/json; charset=utf-8";
}
