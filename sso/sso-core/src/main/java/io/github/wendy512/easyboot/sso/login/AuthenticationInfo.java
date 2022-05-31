package io.github.wendy512.easyboot.sso.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.AuthenticationException;

import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import lombok.Data;

/**
 * @author taowenwu
 * @date 2021-04-18 21:27:21:27
 * @since 1.0.0
 */
@Data
public class AuthenticationInfo {
    private UserInfo userInfo;
    private HttpServletRequest request;
    private AuthenticationException ex;
    private String clientId;
    private String token;
    private String grantType;

    public AuthenticationInfo() {}
    
    public AuthenticationInfo(UserInfo userInfo, HttpServletRequest request, String token, String clientId) {
        this(userInfo, request, null, token, clientId);
    }

    public AuthenticationInfo(UserInfo userInfo, HttpServletRequest request, AuthenticationException ex, String token, String clientId) {
        this.userInfo = userInfo;
        this.request = request;
        this.ex = ex;
        this.token = token;
        this.clientId = clientId;
    }
}
