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
 * 鉴权需要排除的url
 * @author taowenwu
 * @date 2021-04-17 15:19:15:19
 * @since 1.0.0
 */
@Data
public class ExcludeUrl {
    private String resourceExcludeUrl;
    private String securityExcludeUrl;
}
