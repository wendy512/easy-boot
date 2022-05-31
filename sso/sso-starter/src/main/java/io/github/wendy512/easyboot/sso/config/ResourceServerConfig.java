package io.github.wendy512.easyboot.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.sso.manage.ResourceConfigManager;

/**
 * 资源限制定义
 * 
 * @author taowenwu
 * @date 2021-04-02 17:13:17:13
 * @since 1.0.0
 */
@Configuration
@EnableResourceServer
@Order(6)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuthCustomManager customOAuthManager;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ResourceConfigManager configManager = customOAuthManager.getConfigManager();
        http.formLogin().failureHandler(customOAuthManager.getExceptionManager().getFailureHandler());
        http.authorizeRequests()
                .antMatchers(configManager.getResourceExcludeUrls()).permitAll()
                .anyRequest().authenticated();
        if (configManager.getConfiguration().isEnableCsrf()) {
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(configManager.getSecurityExcludeUrls());
        } else {
            http.csrf().disable();
        }
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(configManager.getSecurityExcludeUrls());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(customOAuthManager.getConfigManager().getResourceId())
            .authenticationEntryPoint(customOAuthManager.getExceptionManager().getAuthenticationEntryPoint())
            .accessDeniedHandler(customOAuthManager.getAccessDeniedHandler());
    }
}
