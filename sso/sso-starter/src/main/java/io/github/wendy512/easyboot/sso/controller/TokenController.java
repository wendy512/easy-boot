package io.github.wendy512.easyboot.sso.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.sso.context.AuthenticationContextHolder;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;
import io.github.wendy512.easyboot.sso.login.LoginAuthenticationProvider;
import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.vo.VoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author taowenwu
 * @date 2021-04-09 21:33:21:33
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/oauth")
@SessionAttributes({TokenController.AUTHORIZATION_REQUEST_ATTR_NAME, TokenController.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME})
public class TokenController {

    static final String AUTHORIZATION_REQUEST_ATTR_NAME = "authorizationRequest";

    static final String ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME = "org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST";


    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private OAuthCustomManager oAuthCustomManager;

    @RequestMapping(value = "/token", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<VoResponse<OAuth2AccessToken>> postAccessToken(HttpServletRequest request,
        Principal principal, @RequestParam Map<String, String> parameters,
        @SessionAttribute(value = Constants.SESSION_KEY, required = false) String text)
        throws HttpRequestMethodNotSupportedException {
        ResponseEntity<OAuth2AccessToken> responseOAuth = tokenEndpoint.postAccessToken(principal, parameters);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = AuthenticationContextHolder.getUserInfo();
        String clientId = MapUtils.getString(parameters, "client_id");
        String grantType = MapUtils.getString(parameters, "grant_type");
        IUserService userService = oAuthCustomManager.getUserService();
        AuthenticationInfo authenticationInfo =
            new AuthenticationInfo(userInfo, request, responseOAuth.getBody().getValue(), clientId);
        authenticationInfo.setGrantType(grantType);
        
        // 记录登录日志
        userService.getUserLogService().addLoginLog(authenticationInfo);
        // 更新用户登录时间和IP
        userService.updateLastLogin(authenticationInfo);
        
        ResponseEntity<VoResponse<OAuth2AccessToken>> newResponse =
            new ResponseEntity(VoResponse.builder().ok(responseOAuth.getBody()), responseOAuth.getHeaders(),
                responseOAuth.getStatusCode());
        String verifyCode = MapUtils.getString(parameters, "verifyCode");
        checkVerifyCode((String)text, verifyCode);

        return newResponse;
    }

    private void checkVerifyCode(String text, String input) {
        if (oAuthCustomManager.getConfigManager().enableVerifyCode()) {
            LoginAuthenticationProvider provider =
                (LoginAuthenticationProvider) oAuthCustomManager.getAuthenticationProvider();
            provider.checkVerifyCode(text, input);
        }
    }

}
