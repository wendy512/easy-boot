package io.github.wendy512.easyboot.sso.user.service;

import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-08
 */
public interface IUserService {

    IUserLogService getUserLogService();

    UserInfo getUserInfo(String username);
    
    void updateLastLogin(AuthenticationInfo authenticationInfo);
}
