package io.github.wendy512.easyboot.sso.user.service.impl;

import java.util.Date;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.wendy512.easyboot.common.util.RequestUtils;
import io.github.wendy512.easyboot.sso.custom.handler.RedisTokenEnhancer;
import io.github.wendy512.easyboot.sso.custom.user.UserInfo;
import io.github.wendy512.easyboot.sso.custom.user.UserStatus;
import io.github.wendy512.easyboot.sso.login.AuthenticationInfo;
import io.github.wendy512.easyboot.sso.user.entity.User;
import io.github.wendy512.easyboot.sso.user.mapper.UserMapper;
import io.github.wendy512.easyboot.sso.user.service.IUserLogService;
import io.github.wendy512.easyboot.sso.user.service.IUserService;
import io.github.wendy512.easyboot.sso.util.SpringContextUtil;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    private final UserMapper userMapper;
    private final RedisTokenEnhancer redisTokenEnhancer;

    public UserServiceImpl(UserMapper userMapper, RedisTokenEnhancer redisTokenEnhancer) {
        this.userMapper = userMapper;
        this.redisTokenEnhancer = redisTokenEnhancer;
    }

    @Override
    public IUserLogService getUserLogService() {
        return SpringContextUtil.getContext().getBean(IUserLogService.class);
    }

    @Override
    public UserInfo getUserInfo(String username) {
        User user = this.lambdaQuery().eq(User::getUserName, username).one();
        if (null == user) {
            return null;
        }
        
        boolean enabled = UserStatus.EFFECT == user.getStatus();

        UserInfo userInfo = new UserInfo(username, user.getPassword(), enabled, enabled, enabled, enabled,
            AuthorityUtils.NO_AUTHORITIES);
        userInfo.setSecUid(user.getSecUid());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setEmail(user.getEmail());
        userInfo.setNickName(user.getNickName());
        userInfo.setTelephone(user.getTelephone());
        userInfo.setId(user.getId());
        userInfo.setGender(user.getGender());
        userInfo.setLastLoginIP(user.getLastLoginIP());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        userInfo.setTenantId(user.getTenantId());
        
        return userInfo;
    }

    @Override
    public void updateLastLogin(AuthenticationInfo authenticationInfo) {
        UserInfo userInfo = authenticationInfo.getUserInfo();
        if (null == authenticationInfo.getEx() && null != userInfo) {
            // 更新最后一次的登记时间和登录IP
            String username = userInfo.getUsername();
            String loginIP = RequestUtils.getIP(authenticationInfo.getRequest());

            User user = new User();
            user.setLastLoginTime(new Date());
            user.setLastLoginIP(loginIP);
            
            this.lambdaUpdate().eq(User::getUserName, username).update(user);
        }
    }
}
