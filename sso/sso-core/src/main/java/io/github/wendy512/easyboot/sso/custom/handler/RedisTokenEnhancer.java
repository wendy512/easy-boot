package io.github.wendy512.easyboot.sso.custom.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.easyboot.redis.RedisUtils;
import io.github.wendy512.easyboot.sso.config.SSOConfiguration;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;

/**
 * 自定义增强器
 * @author taowenwu
 * @date 2021-04-10 20:25:20:25
 * @since 1.0.0
 */
@ConditionalOnProperty(prefix = SSOConfiguration.CONFIG_PREFIX, name = "tokenStore", havingValue = "redis", matchIfMissing = false)
@Component
public class RedisTokenEnhancer implements OAuthTokenEnhancer {

    public static final String USER_INFO_REDIS_PREFIX_KEY = "oauth2:user:";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (null != authentication.getUserAuthentication()) {
            UserInfo userInfo = (UserInfo)authentication.getUserAuthentication().getPrincipal();;
            Map<String, Object> additionalInfo = new HashMap<>(5);
            additionalInfo.put("secUid", userInfo.getSecUid());
            additionalInfo.put("avatar", userInfo.getAvatar());
            additionalInfo.put("nickName", userInfo.getNickName());
            additionalInfo.put("gender", userInfo.getGender());
            ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);

            saveUserInfo(accessToken, userInfo);
        }

        return accessToken;
    }

    @Override
    public void saveUserInfo(OAuth2AccessToken accessToken, UserInfo userInfo) {
        RedisUtils.getTemplate().opsForValue().set(USER_INFO_REDIS_PREFIX_KEY + accessToken.getValue(),
                JSON.toJSONString(userInfo), accessToken.getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public UserInfo getUserInfo(OAuth2AccessToken accessToken) {
        String value =
            (String)RedisUtils.getTemplate().opsForValue().get(USER_INFO_REDIS_PREFIX_KEY + accessToken.getValue());
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, UserInfo.class);
        }
        return null;
    }

    @Override
    public void removeUserInfo(OAuth2AccessToken accessToken) {
        RedisUtils.getTemplate().delete(USER_INFO_REDIS_PREFIX_KEY + accessToken);
    }
}
