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

package io.github.wendy512.easyboot.sso;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;

import io.github.wendy512.easyboot.common.util.EnumUtil;
import io.github.wendy512.easyboot.redis.core.RedisApplicationContext;
import io.github.wendy512.easyboot.sso.config.SSOConfiguration;
import io.github.wendy512.easyboot.sso.config.VerifyCodeConfig;
import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.sso.custom.handler.RedisTokenEnhancer;
import io.github.wendy512.easyboot.sso.custom.tokenstore.TokenStoreFactoryBean;
import io.github.wendy512.easyboot.sso.exception.OAuthResponseExceptionTranslator;
import io.github.wendy512.easyboot.sso.login.LoginAuthenticationProvider;
import io.github.wendy512.easyboot.sso.manage.*;
import io.github.wendy512.easyboot.sso.user.mapper.UserMapper;
import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.sso.user.service.impl.UserServiceImpl;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeFactory;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeGenerator;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeType;
import io.github.wendy512.easyboot.webx.WebXStarterAutoConfiguration;
import io.github.wendy512.easyboot.webx.exception.translator.ResponseExceptionTranslator;

/**
 * 自动装配
 * @author wendy512
 * @date 2022-05-27 16:40:54
 * @since 1.0.0
 */
@ComponentScan("io.github.wendy512.easyboot.sso")
@Configuration
@EnableConfigurationProperties(SSOConfiguration.class)
@AutoConfigureBefore(WebXStarterAutoConfiguration.class)
public class SSOStarterAutoConfiguration {

    private final SSOConfiguration configuration;

    public SSOStarterAutoConfiguration(SSOConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean(OAuthCustomManager.class)
    public OAuthCustomManager customOAuthManager(DataSource dataSource, IUserService userService,
        RedisApplicationContext applicationContext, TokenStore tokenStore, OAuthExceptionManager exceptionManager) {

        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);

        ResourceConfigManager configManager = new DefaultResourceConfigManager(configuration);
        TokenManager tokenManager = new DefaultTokenManager(configuration,
            applicationContext.getRedisTemplate(), tokenStore, clientDetailsService, userService);

        DefaultOAuthCustomManager customOAuthManager = new DefaultOAuthCustomManager(clientDetailsService, userService,
            configManager, exceptionManager, tokenManager);

        LoginAuthenticationProvider authenticationProvider =
            (LoginAuthenticationProvider)customOAuthManager.getAuthenticationProvider();
        authenticationProvider.setVerifyCodeValidation(verifyCodeGenerator());
        return customOAuthManager;
    }

    @Bean
    @ConditionalOnMissingBean(OAuthExceptionManager.class)
    public OAuthExceptionManager exceptionManager(ResponseExceptionTranslator exceptionTranslator) {
        return new DefaultOAuthExceptionManager(exceptionTranslator);
    }

    @ConditionalOnMissingBean(IUserService.class)
    @Bean
    public IUserService userService(UserMapper userMapper, RedisTokenEnhancer redisTokenEnhancer) {
        return new UserServiceImpl(userMapper, redisTokenEnhancer);
    }

    @ConditionalOnMissingBean(VerifyCodeGenerator.class)
    @Bean
    public VerifyCodeGenerator verifyCodeGenerator() {
        VerifyCodeConfig verifyCodeConfig = configuration.getVerifyCode();
        Optional<VerifyCodeType> opt =
                EnumUtil.getEnumObject(VerifyCodeType.class, e -> e.getType().equals(verifyCodeConfig.getType()));
        return VerifyCodeFactory.createGenerator(opt.get());
    }

    @Bean(Constants.SSOTOKENSTORE_BEAN_ID)
    public TokenStoreFactoryBean tokenStore(RedisApplicationContext applicationContext) {
        return new TokenStoreFactoryBean(configuration, applicationContext.getRedisTemplate());
    }
    
    @Bean
    public ResponseExceptionTranslator responseExceptionTranslator() {
        return new OAuthResponseExceptionTranslator();
    }

    /**
     * 将返回的英文认证信息修改为中文
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // 指定类路径的时候，不需要添加文件后缀[.properties]
        messageSource.setBasename("classpath*:org/springframework/security/messages_zh_CN");
        return messageSource;
    }
    
    
}
