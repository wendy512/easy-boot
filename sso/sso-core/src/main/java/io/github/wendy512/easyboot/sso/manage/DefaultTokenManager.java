package io.github.wendy512.easyboot.sso.manage;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;

import io.github.wendy512.easyboot.sso.config.SSOConfiguration;
import io.github.wendy512.easyboot.sso.custom.RedisAuthenticationCodeServices;
import io.github.wendy512.easyboot.sso.custom.handler.OAuthTokenEnhancer;
import io.github.wendy512.easyboot.sso.custom.token.CustomTokenServices;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;
import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.sso.util.SpringContextUtil;

/**
 *  token 管理
 * @author taowenwu
 * @date 2021-04-16 13:16:13:16
 * @since 1.0.0
 */
public class DefaultTokenManager implements TokenManager, InitializingBean {

    private final DefaultTokenServices tokenService;
    private final TokenStore tokenStore;
    private final AuthorizationCodeServices authorizationCodeServices;
    private final IUserService userService;

    public DefaultTokenManager(SSOConfiguration configuration, RedisTemplate redisTemplate,
        TokenStore tokenStore, ClientDetailsService clientDetailsService, IUserService userService) {
        this.tokenStore = tokenStore;
        // 配置TokenServices参数
        // accessTokenValiditySeconds ： access_token 过期时间：5s
        // reuseRefreshToken，refreshTokenValiditySeconds：refresh_token 过期时间，默认不过期
        this.tokenService = new CustomTokenServices();
        this.tokenService.setTokenStore(tokenStore);
        this.tokenService.setClientDetailsService(clientDetailsService);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(getTokenEnhancer()));
        // support refresh token
        this.tokenService.setSupportRefreshToken(true);
        // 是否能重复使用 refresh_token
        this.tokenService.setReuseRefreshToken(false);
        this.tokenService.setTokenEnhancer(tokenEnhancerChain);
        this.authorizationCodeServices = new RedisAuthenticationCodeServices(redisTemplate, configuration);
        this.userService = userService;
    }

    private OAuthTokenEnhancer getTokenEnhancer() {
        return SpringContextUtil.getContext().getBean(OAuthTokenEnhancer.class);
    }

    @Override
    public AuthorizationCodeServices getAuthorizationCodeServices() {
        return this.authorizationCodeServices;
    }

    @Override
    public DefaultTokenServices getTokenService() {
        return this.tokenService;
    }

    @Override
    public TokenStore getTokenStore() {
        return this.tokenStore;
    }

    @Override
    public boolean revokeToken(HttpServletRequest request, Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)authentication.getDetails();
        OAuth2Request oAuth2Request = ((OAuth2Authentication)authentication).getOAuth2Request();
        String clientId = oAuth2Request.getClientId();
        AuthenticationInfo authenticationInfo =
            new AuthenticationInfo((UserInfo)authentication.getPrincipal(), request, details.getTokenValue(), clientId);
        authenticationInfo.setGrantType(oAuth2Request.getGrantType());
        OAuth2AccessToken accessToken = tokenStore.getAccessToken((OAuth2Authentication)authentication);
        getTokenEnhancer().removeUserInfo(accessToken);
        // 登出日志
        this.userService.getUserLogService().addLogoutLog(authenticationInfo);
        // 移除token
        return this.tokenService.revokeToken(details.getTokenValue());
    }

    @Override
    public void afterPropertiesSet() throws Exception {}
}
