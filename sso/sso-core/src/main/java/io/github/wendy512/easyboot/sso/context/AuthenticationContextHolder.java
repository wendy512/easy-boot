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

package io.github.wendy512.easyboot.sso.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;

/**
 * 鉴权信息上下文
 * @author wendy512
 * @date 2022-05-30 16:31:09
 * @since 1.0.0
 */
public final class AuthenticationContextHolder {
    
    private AuthenticationContextHolder() {}
    
    private static final ThreadLocal<UserInfo> LOCAL_USER_INFO = new TransmittableThreadLocal<>();
    
    public static void setUserInfo(UserInfo userInfo) {
        LOCAL_USER_INFO.set(userInfo);
    }
    
    public static UserInfo getUserInfo() {
        return LOCAL_USER_INFO.get();
    }
}
