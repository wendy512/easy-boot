package io.github.wendy512.easyboot.sso.manage;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.Assert;

import io.github.wendy512.easyboot.sso.custom.handler.CustomAccessDeniedHandler;
import io.github.wendy512.easyboot.sso.custom.user.UserDetailsServiceImpl;
import io.github.wendy512.easyboot.sso.login.LoginAuthenticationProvider;
import io.github.wendy512.easyboot.sso.user.service.IUserService;

/**
 * 鉴权相关类管理
 * @author taowenwu
 * @date 2021-04-16 13:19:13:19
 * @since 1.0.0
 */
public class DefaultOAuthCustomManager implements OAuthCustomManager, InitializingBean {

    private PasswordEncoder passwordEncoder;

    private ClientDetailsService clientDetailsService;

    private UserDetailsService userDetailsService;

    private AccessDeniedHandler accessDeniedHandler;

    private OAuthExceptionManager exceptionManager;

    private TokenManager tokenManager;

    private ResourceConfigManager configManager;

    private DaoAuthenticationProvider authenticationProvider;

    private IUserService userService;

    public DefaultOAuthCustomManager(ClientDetailsService clientDetailsService, IUserService userService,
                                     ResourceConfigManager configManager, OAuthExceptionManager exceptionManager, TokenManager tokenManager) {
        this.passwordEncoder = new BCryptPasswordEncoder(5);
        this.userDetailsService = new UserDetailsServiceImpl(userService);
        this.accessDeniedHandler = new CustomAccessDeniedHandler();
        this.authenticationProvider = new LoginAuthenticationProvider(this.userDetailsService, userService);
        this.authenticationProvider.setPasswordEncoder(this.passwordEncoder);
        this.authenticationProvider.setUserDetailsService(this.userDetailsService);
        this.authenticationProvider.setHideUserNotFoundExceptions(false);
        this.userService = userService;
        this.clientDetailsService = clientDetailsService;
        this.configManager = configManager;
        this.tokenManager = tokenManager;
        this.exceptionManager = exceptionManager;
        try {
            this.authenticationProvider.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    @Override
    public ClientDetailsService getClientDetailsService() {
        return this.clientDetailsService;
    }

    @Override
    public UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    @Override
    public IUserService getUserService() {
        return this.userService;
    }

    @Override
    public AccessDeniedHandler getAccessDeniedHandler() {
        return this.accessDeniedHandler;
    }

    @Override
    public OAuthExceptionManager getExceptionManager() {
        return this.exceptionManager;
    }

    @Override
    public DaoAuthenticationProvider getAuthenticationProvider() {
        return this.authenticationProvider;
    }

    @Override
    public TokenManager getTokenManager() {
        return this.tokenManager;
    }

    @Override
    public ResourceConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenManager, "tokenManager 不能为空");
        Assert.notNull(configManager, "configManager 不能为空");
        Assert.notNull(exceptionManager, "authExceptionManager 不能为空");
    }
}
