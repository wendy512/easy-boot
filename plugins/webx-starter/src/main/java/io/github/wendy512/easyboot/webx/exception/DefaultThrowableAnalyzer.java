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

package io.github.wendy512.easyboot.webx.exception;

import javax.servlet.ServletException;

/**
 * copy from org.springframework.security.oauth2.common.DefaultThrowableAnalyzer
 * @author wendy512
 * @date 2022-05-26 16:48:02
 * @since 1.0.0
 */
public final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
    
    protected void initExtractorMap() {
        super.initExtractorMap();

        registerExtractor(ServletException.class, new ThrowableCauseExtractor() {
            public Throwable extractCause(Throwable throwable) {
                ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
                return ((ServletException) throwable).getRootCause();
            }
        });
    }
}
