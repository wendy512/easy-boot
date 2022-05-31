package io.github.wendy512.easyboot.sso.custom;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import io.github.wendy512.easyboot.sso.config.SSOConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * @author taowenwu
 * @date 2021-04-11 17:23:17:23
 * @since 1.0.0
 */
@Slf4j
public class RedisAuthenticationCodeServices extends RandomValueAuthorizationCodeServices {

    private static final String AUTH_CODE_KEY = "auth-code";
    private RedisTemplate redisTemplate;
    private SSOConfiguration configuration;
    private RandomValueStringGenerator generator = new RandomValueStringGenerator(10);

    public RedisAuthenticationCodeServices(RedisTemplate redisTemplate, SSOConfiguration configuration) {
        Assert.notNull(redisTemplate, "redisTemplate required");
        this.redisTemplate = redisTemplate;
        this.configuration = configuration;
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.generate();
        this.store(code, authentication);
        return code;
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.execute((RedisCallback) conn -> {
            conn.hSet(getKey().getBytes(StandardCharsets.UTF_8), code.getBytes(StandardCharsets.UTF_8),
                    SerializationUtils.serialize(authentication));
            return null;
        });
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        return (OAuth2Authentication) redisTemplate.execute((RedisCallback) conn -> {
            OAuth2Authentication authentication = null;
            
            try {
                authentication = (OAuth2Authentication)SerializationUtils.deserialize(
                        conn.hGet(getKey().getBytes(StandardCharsets.UTF_8), code.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                log.error("", e);
                return null;
            }

            if (null != authentication) {
                conn.hDel(getKey().getBytes(StandardCharsets.UTF_8), code.getBytes(StandardCharsets.UTF_8));
            }
            
            return authentication;
        });
    }

    private String getKey () {
        return configuration.getResourceId() + "-" + AUTH_CODE_KEY;
    }
}
