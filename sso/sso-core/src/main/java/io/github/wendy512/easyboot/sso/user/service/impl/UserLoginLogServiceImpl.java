package io.github.wendy512.easyboot.sso.user.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import io.github.wendy512.easyboot.common.util.RequestUtils;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;
import io.github.wendy512.easyboot.sso.user.entity.UserLoginLog;
import io.github.wendy512.easyboot.sso.user.entity.UserLoginLog.LogType;
import io.github.wendy512.easyboot.sso.user.mapper.UserLoginLogMapper;
import io.github.wendy512.easyboot.sso.user.service.IUserLogService;
import io.github.wendy512.easyboot.vo.ResponseCode;

/**
 * @author taowenwu
 * @date 2021-04-18 17:38:17:38
 * @since 1.0.0
 */
@Service
public class UserLoginLogServiceImpl implements IUserLogService {
    
    private final UserLoginLogMapper userLoginLogMapper;

    public UserLoginLogServiceImpl(UserLoginLogMapper userLoginLogMapper) {
        this.userLoginLogMapper = userLoginLogMapper;
    }

    @Override
    public void addLoginLog(AuthenticationInfo authenticationInfo) {
        UserLoginLog userLoginLog = buildUserLog(authenticationInfo, LogType.WEB_LOGIN);
        userLoginLogMapper.insert(userLoginLog);
    }

    @Override
    public void addLogoutLog(AuthenticationInfo authenticationInfo) {
        UserLoginLog userLogoutLog = buildUserLog(authenticationInfo, LogType.WEB_LOGOUT);
        userLoginLogMapper.insert(userLogoutLog);
    }


    private UserLoginLog buildUserLog(AuthenticationInfo authenticationInfo, LogType logType) {
        UserInfo userInfo = authenticationInfo.getUserInfo();
        HttpServletRequest request = authenticationInfo.getRequest();
        String userAgent = request.getHeader("User-Agent");
        UserLoginLog loginLog = new UserLoginLog();

        /*if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Map details = (Map) authentication.getDetails();
            clientId = MapUtils.getString(details, "client_id");
            loginLog.setGrantType(MapUtils.getString(details, "grant_type"));
        } else {
            clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();
        }*/

        if (null != userInfo) {
            loginLog.setSecUid(userInfo.getSecUid());
        }
        loginLog.setClientId(authenticationInfo.getClientId());
        loginLog.setGrantType(authenticationInfo.getGrantType());
        loginLog.setUserAgent(userAgent);
        loginLog.setCreateTime(new Date());
        loginLog.setType(logType.getName());
        loginLog.setSourceIP(RequestUtils.getIP(request));
        if (null != userInfo) {
            loginLog.setTenantId(userInfo.getTenantId());
        }
        loginLog.setToken(authenticationInfo.getToken());
        
        AuthenticationException ex = authenticationInfo.getEx();
        if (null == ex) {
            loginLog.setResultCode(ResponseCode.SUCCESS.getCode());
        } else {
            loginLog.setResultCode(ResponseCode.AUTH_ERROR.getCode());
            loginLog.setResultDesc(ex.getMessage());
        }
        return loginLog;
    }
}
