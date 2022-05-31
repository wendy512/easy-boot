package io.github.wendy512.easyboot.sso.manage;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import io.github.wendy512.easyboot.sso.custom.filter.CustomAuthenticationEntryPoint;
import io.github.wendy512.easyboot.sso.custom.handler.CustomAuthenticationFailureHandler;
import io.github.wendy512.easyboot.webx.exception.translator.ResponseExceptionTranslator;

/**
 * @author taowenwu
 * @date 2021-04-16 13:18:13:18
 * @since 1.0.0
 */
public class DefaultOAuthExceptionManager implements OAuthExceptionManager, InitializingBean {
    
    private AuthenticationFailureHandler failureHandler;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationEntryPoint entryPoint;
    
    public DefaultOAuthExceptionManager(ResponseExceptionTranslator exceptionTranslator) {
        this.failureHandler = new CustomAuthenticationFailureHandler();
        this.entryPoint = new CustomAuthenticationEntryPoint(exceptionTranslator);
    }

    @Override
    public AuthenticationFailureHandler getFailureHandler() {
        return this.failureHandler;
    }

    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return this.successHandler;
    }

    @Override
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.entryPoint;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(failureHandler, "exceptionTranslator 不能为空");
        Assert.notNull(entryPoint, "exceptionTranslator 不能为空");
    }
}
