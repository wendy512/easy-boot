package io.github.wendy512.easyboot.sso.manage;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;

import io.github.wendy512.easyboot.sso.user.service.IUserService;

/**
 * 自定义统一管理接口
 * 
 * @author taowenwu
 * @date 2021-04-16 11:19:11:19
 * @since 1.0.0
 */
public interface OAuthCustomManager {
    PasswordEncoder getPasswordEncoder();

    ClientDetailsService getClientDetailsService();

    UserDetailsService getUserDetailsService();

    IUserService getUserService();

    AccessDeniedHandler getAccessDeniedHandler();

    DaoAuthenticationProvider getAuthenticationProvider();

    OAuthExceptionManager getExceptionManager();

    TokenManager getTokenManager();

    ResourceConfigManager getConfigManager();

}
