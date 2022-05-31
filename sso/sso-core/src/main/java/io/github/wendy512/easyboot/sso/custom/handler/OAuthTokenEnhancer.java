package io.github.wendy512.easyboot.sso.custom.handler;

import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public interface OAuthTokenEnhancer extends TokenEnhancer {
    
    void saveUserInfo(OAuth2AccessToken accessToken, UserInfo userInfo);
    
    UserInfo getUserInfo(OAuth2AccessToken accessToken); 
    
    void removeUserInfo(OAuth2AccessToken accessToken);
}
