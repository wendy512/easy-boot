package io.github.wendy512.easyboot.sso.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.github.wendy512.easyboot.sso.context.AuthenticationContextHolder;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeValidation;

/**
 * 自定义校验用户实现类
 * 
 * @author taowenwu
 * @date 2021-04-09 14:25:14:25
 * @since 1.0.0
 */
public class LoginAuthenticationProvider extends DaoAuthenticationProvider {
    
    private IUserService userService;
    private VerifyCodeValidation verifyCodeValidation;
    private UserDetailsPasswordService userDetailsPasswordService;

    public LoginAuthenticationProvider(UserDetailsService userDetailsService, IUserService userService) {
        super();
        this.userService = userService;
        // 这个地方一定要对userDetailsService赋值，不然userDetailsService是null (这个坑有点深)
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserInfo userInfo = (UserInfo)userDetails;
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        try {
            checkPassword(userDetails, authentication, userInfo);
            doLoginSuccess(authentication, userInfo);
        } catch (AuthenticationException e) {
            //登录失败情况下
            doLoginFailed(userInfo, authentication, request, e);
            throw e;
        }

    }
    
    protected void doLoginSuccess(Authentication authentication, UserInfo userInfo) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthenticationContextHolder.setUserInfo(userInfo);
    }

    protected void doLoginFailed(UserInfo userInfo, Authentication authentication, HttpServletRequest request,
        AuthenticationException ex) {
        Map details = (Map) authentication.getDetails();
        String clientId = MapUtils.getString(details, "client_id");
        String grantType = MapUtils.getString(details, "grant_type");
        
        AuthenticationInfo authenticationInfo = new AuthenticationInfo(userInfo, request, ex, null, clientId);
        authenticationInfo.setGrantType(grantType);
        // 记录登录日志
        userService.getUserLogService().addLoginLog(authenticationInfo);
    }

    protected void checkPassword(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication,
        UserInfo userInfo) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        PasswordEncoder passwordEncoder = getPasswordEncoder();
        
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    public void checkVerifyCode(String text, String input) {
        verifyCodeValidation.validate(text, input);
    }

    public void setVerifyCodeValidation(VerifyCodeValidation validation) {
        this.verifyCodeValidation = validation;
    }

    @Override
    public void setUserDetailsPasswordService(UserDetailsPasswordService userDetailsPasswordService) {
        super.setUserDetailsPasswordService(userDetailsPasswordService);
        this.userDetailsPasswordService = userDetailsPasswordService;
    }
}
