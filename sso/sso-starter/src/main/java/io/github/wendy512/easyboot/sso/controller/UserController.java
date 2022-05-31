package io.github.wendy512.easyboot.sso.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.manage.OAuthCustomManager;
import io.github.wendy512.easyboot.vo.VoResponse;

/**
 * 用户信息控制层
 * @author taowenwu
 * @date 2021-04-21 11:05:11:05
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private OAuthCustomManager customOAuthManager;

    @GetMapping("/info")
    public VoResponse getUserInfo(Principal principal) {
        UserInfo userInfo = (UserInfo) customOAuthManager.getUserService().getUserInfo(principal.getName());
        if (null != userInfo) {
            userInfo.eraseCredentials();
        }
        return VoResponse.builder().ok(userInfo);
    }
}
