package io.github.wendy512.easyboot.sso.user.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import io.github.wendy512.easyboot.mybatisplus.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 安全的id，防止通过自增id猜到用户的id
     */
    private String secUid;

    /**
     * 密码
     */
    private String password;

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
     * 1000：有效	            
     * 1010：锁定	            
     * 1001：暂停	           
     * 1011：密码错误锁定	            
     * 1020：停用	            
     * 1100：无效	            
     * 1200：未生效	            
     * 1210：审批通过	            
     * 1220：审批中	            
     * 1230：审批未通过	            
     * 1231：注销	            
     * 1232：离职
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 修改用户
     */
    private String modifyUser;

    /**
     * 最后登录的时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录的IP
     */
    @TableField("last_login_ip")
    private String lastLoginIP;

    /**
     * 租户id
     */
    private Long tenantId;
}
