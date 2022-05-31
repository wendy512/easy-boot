package io.github.wendy512.easyboot.sso.manage;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * token 管理器
 * 
 * @author taowenwu
 * @date 2021-04-16 11:31:11:31
 * @since 1.0.0
 */
public interface TokenManager {

    AuthorizationCodeServices getAuthorizationCodeServices();

    DefaultTokenServices getTokenService();

    TokenStore getTokenStore();

    boolean revokeToken(HttpServletRequest request, Authentication authentication);

}
