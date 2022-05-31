package io.github.wendy512.easyboot.sso.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.sso.custom.filter.CustomClientCredentialsTokenEndpointFilter;
import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.sso.manage.TokenManager;
import io.github.wendy512.easyboot.sso.user.mapper.UserMapper;

/**
 * 鉴权server端配置
 * @formatter:off
 * grant_type种类
 * <p>
 * 1、authorization_code — 授权码模式(即先登录获取code,再获取token)
 *  a) 先调用http://localhost:9100/oauth/authorize?response_type=code&client_id=web-client&redirect_uri=http://www.baidu.com&scope=all&state=1000，获取code
 *  b) 再调用/oauth/token获取token
 *     参数：grant_type=authorization_code&client_id=web-client&client_secret=e10adc3949ba59abbe56e057f20f883e&code=xx
 * </p>
 * 
 * <p>
 * 2、password — 密码模式(将用户名,密码传过去,直接获取token)
 * 参数：grant_type=password&username=xx&password=xx&client_id=xx&client_secret=xx
 * </p>
 *
 * <p>
 * 3、client_credentials — 客户端模式(无用户,用户向客户端注册,然后客户端以自己的名义向’服务端’获取资源)
 *  a) 先调用http://localhost:9100/oauth/authorize?response_type=code&client_id=web-client&redirect_uri=http://www.baidu.com&scope=all&state=1000，获取code
 *  b) 再调用/oauth/token获取token
 *     参数：grant_type=client_credentials&client_id=web-client&client_secret=e10adc3949ba59abbe56e057f20f883e&code=xx
 * </p>
 *
 * <p>
 * 4、implicit — 简化模式(在redirect_uri 的Hash传递token; Auth客户端运行在浏览器中,如JS,Flash)
 *  a) 先调用 http://localhost:9100/oauth/authorize?response_type=token&client_id=web-client&redirect_uri=http://www.baidu.com&scope=all&state=1000
 *  b)跳转完成后的地址栏：https://www.baidu.com/#access_token=38bcca5c-16c9-46b2-9c60-d74ff8b61b9c&token_type=bearer&state=1000&expires_in=3599&nickName=taowenwu&username=taowenwu
 * </p>
 * 
 * <p>
 * 5、refresh_token — 刷新access_token
 * 参数：grant_type=refresh_token&refresh_token=xx&client_id=xx&client_secret=xx
 * </p>
 *
 * @formatter:on
 *
 *      * /oauth/authorize：授权端点
 *      * /oauth/token：令牌端点
 *      * /oauth/confirm_access：用户确认授权提交端点
 *      * /oauth/error：授权服务错误信息端点
 *      * /oauth/check_token?token=xxx ：用于资源服务访问的令牌解析端点
 *      * /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * webSecurityConfig 中配置的AuthenticationManager
     */
    @Autowired
    @Qualifier(Constants.AUTHENTICATIONMANAGER_BEAN_ID)
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OAuthCustomManager customOAuthManager;

    @Autowired
    private UserMapper userMapper;

    /**
     * 对 oauth_client_details 表的一些操作
     *
     * @return ClientDetailsService
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        
        security.tokenKeyAccess("permitAll()")
            // 不允许check_token访问
            .checkTokenAccess("denyAll()")
            .passwordEncoder(customOAuthManager.getPasswordEncoder());
        // 允许表单登录，如果要自定异常格式，此处必须注释
        if (customOAuthManager.getConfigManager().allowFormAuthenticationForClients()) {
            security.allowFormAuthenticationForClients();
        }
        CustomClientCredentialsTokenEndpointFilter tokenEndpointFilter =
            new CustomClientCredentialsTokenEndpointFilter(security);
        tokenEndpointFilter.afterPropertiesSet();
        tokenEndpointFilter.setAuthenticationFailureHandler(customOAuthManager.getExceptionManager().getFailureHandler());
        //tokenEndpointFilter.setAuthenticationEntryPoint(entryPoint);
        // 客户端认证之前的过滤器
        security.addTokenEndpointAuthenticationFilter(tokenEndpointFilter);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenManager tokenManager = customOAuthManager.getTokenManager();
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenManager.getTokenStore())
                .userDetailsService(customOAuthManager.getUserDetailsService())
                .tokenServices(tokenManager.getTokenService())
                //.exceptionTranslator(customOAuthManager.getExceptionManager().getExceptionTranslator())
                .tokenGranter(new CompositeTokenGranter(getCustomizedTokenGranters()))
                .authorizationCodeServices(tokenManager.getAuthorizationCodeServices())
                .setClientDetailsService(customOAuthManager.getClientDetailsService());
    }

    private List<TokenGranter> getCustomizedTokenGranters() {
        
        ClientDetailsService clientDetailsService = customOAuthManager.getClientDetailsService();
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        TokenManager tokenManager = customOAuthManager.getTokenManager();
        AuthorizationCodeTokenGranter authorizationCodeTokenGranter =
            new AuthorizationCodeTokenGranter(tokenManager.getTokenService(),
                tokenManager.getAuthorizationCodeServices(), clientDetailsService, requestFactory);
        RefreshTokenGranter refreshTokenGranter =
            new RefreshTokenGranter(tokenManager.getTokenService(), clientDetailsService, requestFactory);
        ImplicitTokenGranter implicit =
            new ImplicitTokenGranter(tokenManager.getTokenService(), clientDetailsService, requestFactory);
        ClientCredentialsTokenGranter clientCredentialsTokenGranter =
            new ClientCredentialsTokenGranter(tokenManager.getTokenService(), clientDetailsService, requestFactory);
        // 设置返回refresh code
        clientCredentialsTokenGranter.setAllowRefresh(true);

        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(authorizationCodeTokenGranter);
        tokenGranters.add(refreshTokenGranter);
        tokenGranters.add(implicit);
        tokenGranters.add(clientCredentialsTokenGranter);
        tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenManager.getTokenService(),
            clientDetailsService, requestFactory));

        return tokenGranters;
    }

}
