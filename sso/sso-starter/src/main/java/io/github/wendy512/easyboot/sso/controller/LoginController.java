package io.github.wendy512.easyboot.sso.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.vo.ResponseCode;
import io.github.wendy512.easyboot.vo.VoResponse;

/**
 * 登录登出操作
 * @author taowenwu
 * @date 2021-04-11 16:10:16:10
 * @since 1.0.0
 */
@Controller
@RequestMapping("/oauth")
public class LoginController {

    @Autowired
    private OAuthCustomManager customOAuthManager;

    @RequestMapping("/logout")
    @ResponseBody
    public VoResponse logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        new SecurityContextLogoutHandler().logout(request, null, null);
        boolean revokeToken =
            customOAuthManager.getTokenManager().revokeToken(request, (OAuth2Authentication)principal);
        if (revokeToken) {
            return VoResponse.builder().ok();
        } else {
            return VoResponse.builder().resp(ResponseCode.AUTH_LOGOUT_ERROR).build();
        }
    }
}
