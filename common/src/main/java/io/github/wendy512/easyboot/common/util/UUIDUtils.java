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

package io.github.wendy512.easyboot.common.util;

import java.util.UUID;

/**
 * uuid 工具类
 * @author wendy512
 * @date 2022-05-23 17:44:14
 * @since 1.0.0
 */
public final class UUIDUtils {
    
    private UUIDUtils() {}
    
    public static String randomUUID() {
        String uuid = UUID.randomUUID().toString();
        return new StringBuilder(uuid.substring(0, 8)).append(uuid.substring(9, 13)).append(uuid.substring(14, 18))
            .append(uuid.substring(19, 23)).append(uuid.substring(24)).toString();
    }
}
