package io.github.wendy512.easyboot.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.sso.manage.ResourceConfigManager;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuthCustomManager customOAuthManager;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置用户签名服务 主要是user-details 机制，
     *
     * @param auth
     *            签名管理器构造器，用于构建用户具体权限控制
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customOAuthManager.getAuthenticationProvider())
            .userDetailsService(customOAuthManager.getUserDetailsService())
            .passwordEncoder(customOAuthManager.getPasswordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ResourceConfigManager configManager = customOAuthManager.getConfigManager();
        http.requestMatchers()
            .antMatchers(configManager.getSecurityExcludeUrls()).and()
            .authorizeRequests().and().formLogin().permitAll()
            .and().cors().and().csrf().disable();
        /*http.formLogin().loginProcessingUrl(configManager.getLoginProcessingUrl()).and().authorizeRequests()
            .antMatchers(configManager.getSecurityExcludeUrls()).permitAll().anyRequest().permitAll().and().cors();*/
        if (configManager.getConfiguration().isEnableCsrf()) {
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(configManager.getSecurityExcludeUrls());
        } else {
            http.csrf().disable();
        }
        
        http.logout().logoutUrl(configManager.getLogoutUrl());
        // 禁用http basic登录
        http.httpBasic().disable();
        //http.securityContext().securityContextRepository()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.exceptionHandling()
                //.authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customOAuthManager.getAccessDeniedHandler());
    }
}
