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

package io.github.wendy512.easyboot.sso.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import io.github.wendy512.easyboot.vo.ResponseCode;
import io.github.wendy512.easyboot.vo.VoResponse;
import io.github.wendy512.easyboot.webx.exception.translator.DefaultResponseExceptionTranslator;

/**
 * OAuth2异常统一转换
 * @author wendy512
 * @date 2022-05-27 17:38:32
 * @since 1.0.0
 */
public class OAuthResponseExceptionTranslator extends DefaultResponseExceptionTranslator {

    @Override
    public ResponseEntity<VoResponse> translate(Exception e) {
        ResponseEntity<VoResponse> entity = new ResponseEntity<>(HttpStatus.OK);

        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
        Exception ex = (ClientRegistrationException)throwableAnalyzer
            .getFirstThrowableOfType(ClientRegistrationException.class, causeChain);
        if (null != ex) {
            return ResponseEntity
                .ok(VoResponse.builder().resp(ResponseCode.AUTH_ERROR_USER_PASSWORD).msg(ex.getMessage()).build());
        }

        ex = (InsufficientAuthenticationException)throwableAnalyzer.getFirstThrowableOfType(InsufficientAuthenticationException.class,
                causeChain);
        if (ex != null) {
            return ResponseEntity.ok(VoResponse.builder().code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .msg("请先登录！").build());
        }
        
        ex = (AuthenticationException)throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
            causeChain);
        if (ex != null) {
            return ResponseEntity.ok(VoResponse.builder().code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .msg(ex.getMessage()).build());
        }

        ex = (InvalidTokenException)throwableAnalyzer.getFirstThrowableOfType(InvalidTokenException.class,
                causeChain);
        if (ex != null) {
            return ResponseEntity.ok(VoResponse.builder().code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .msg("无效的Token").build());
        }

        ex = (AccessDeniedException)throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ex != null) {
            return ResponseEntity.ok(
                VoResponse.builder().code(String.valueOf(HttpStatus.FORBIDDEN.value())).msg(ex.getMessage()).build());
        }

        ex = (InvalidGrantException)throwableAnalyzer.getFirstThrowableOfType(InvalidGrantException.class, causeChain);
        if (ex != null) {
            return ResponseEntity.ok(
                    VoResponse.builder().resp(ResponseCode.AUTH_ERROR_USER_PASSWORD).build());
        }

        if (ex instanceof OAuth2Exception) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cache-Control", "no-store");
            headers.set("Pragma", "no-cache");
            if (ex instanceof AccessDeniedException || (e instanceof InsufficientScopeException)) {
                headers.set("WWW-Authenticate",
                    String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, ((OAuth2Exception)ex).getSummary()));
            }

            return ResponseEntity.ok(VoResponse.builder().code(String.valueOf(((OAuth2Exception)ex).getHttpErrorCode()))
                .msg(ex.getMessage()).build());
        }

        return super.translate(e);
    }
}
