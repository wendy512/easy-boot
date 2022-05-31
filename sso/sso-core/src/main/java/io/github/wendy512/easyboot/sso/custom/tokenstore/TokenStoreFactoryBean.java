package io.github.wendy512.easyboot.sso.custom.tokenstore;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.github.wendy512.easyboot.sso.config.SSOConfiguration;
import io.github.wendy512.easyboot.sso.constants.Constants;

/**
 * tokenstore 工具
 * 
 * @author taowenwu
 * @date 2021-04-10 19:11:19:11
 * @since 1.0.0
 */
public class TokenStoreFactoryBean implements FactoryBean<TokenStore>, ApplicationContextAware {

    private ApplicationContext context;

    private SSOConfiguration configuration;

    private RedisTemplate redisTemplate;

    public TokenStoreFactoryBean(SSOConfiguration ssoConfiguration) {
        this.configuration = ssoConfiguration;
    }

    public TokenStoreFactoryBean(SSOConfiguration ssoConfiguration, RedisTemplate redisTemplate) {
        this.configuration = ssoConfiguration;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenStore getObject() throws Exception {
        if (Constants.TOKEN_STORE_JDBC.equals(configuration.getTokenStore())) {
            return createJDBCStore();
        } else if (Constants.TOKEN_STORE_REDIS.equals(configuration.getTokenStore())) {
            return createRedisStore();
        } else {
            return createJWTStore();
        }
    }

    @Override
    public Class<?> getObjectType() {
        if (Constants.TOKEN_STORE_JDBC.equals(configuration.getTokenStore())) {
            return JdbcTokenStore.class;
        } else if (Constants.TOKEN_STORE_REDIS.equals(configuration.getTokenStore())) {
            return RedisTokenStore.class;
        } else {
            return JwtTokenStore.class;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private TokenStore createJDBCStore() {
        return new JdbcTokenStore(context.getBean(DataSource.class));
    }

    private TokenStore createJWTStore() {
        return new JwtTokenStore(context.getBean(JwtAccessTokenConverter.class));
    }

    private TokenStore createRedisStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisTemplate);
        redisTokenStore.setPrefix(configuration.getResourceId() + "-");
        //redisTokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator(configuration.getTokenPrivateKey()));
        return redisTokenStore;
    }
}
