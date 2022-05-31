package io.github.wendy512.easyboot.sso.custom.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.vo.ResponseCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author taowenwu
 * @date 2021-04-02 17:02:17:02
 * @since 1.0.0
 */
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private IUserService userService;

    public UserDetailsServiceImpl(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userService.getUserInfo(username);
        if (null == userInfo) {
            throw new BadCredentialsException(ResponseCode.AUTH_ERROR_USER_PASSWORD.getMsg());
        }
        return userInfo;
    }

    public IUserService getUserService() {
        return userService;
    }
}
