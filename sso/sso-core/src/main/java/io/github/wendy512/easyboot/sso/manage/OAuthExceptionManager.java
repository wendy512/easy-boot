package io.github.wendy512.easyboot.sso.manage;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author taowenwu
 * @date 2021-04-16 11:38:11:38
 * @since 1.0.0
 */
public interface OAuthExceptionManager {
    AuthenticationFailureHandler getFailureHandler();

    AuthenticationSuccessHandler getSuccessHandler();

    AuthenticationEntryPoint getAuthenticationEntryPoint();
}
