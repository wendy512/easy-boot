package io.github.wendy512.easyboot.sso.custom.user;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.wendy512.easyboot.common.util.DateUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 * 
 * @author taowenwu
 * @date 2021-04-02 17:03:17:03
 * @since 1.0.0
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo extends User {
    /**
     * 用户ID
     */
    private Long id;

    private String secUid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String telephone;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 1->男；2->女
     */
    private Integer gender;

    /**
     * 最后登录的时间
     */
    @JsonFormat(pattern = DateUtils.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS)
    private Date lastLoginTime;

    /**
     * 最后登录的IP
     */
    private String lastLoginIP;

    private Long tenantId;

    public UserInfo() {
        super("", "", true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
    }

    public UserInfo(String username, String password,
                Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public UserInfo(String username, String password, boolean enabled, boolean accountNonExpired,
        boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.username = username;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.setId(null);
    }
}
