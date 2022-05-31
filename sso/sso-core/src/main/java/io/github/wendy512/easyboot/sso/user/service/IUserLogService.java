package io.github.wendy512.easyboot.sso.user.service;

import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;

/**
 * 用户日志记录类
 * @author taowenwu
 * @date 2021-04-18 17:14:17:14
 * @since 1.0.0
 */
public interface IUserLogService {
    void addLoginLog(AuthenticationInfo authenticationInfo);

    void addLogoutLog(AuthenticationInfo authenticationInfo);
}
