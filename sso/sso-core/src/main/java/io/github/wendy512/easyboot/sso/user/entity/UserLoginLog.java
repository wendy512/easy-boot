package io.github.wendy512.easyboot.sso.user.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户登录日志表
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_login_log")
public class UserLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户标识
     */
    private String secUid;

    /**
     * 操作类型
     * web_login：web登录
     * web_lout：web登出
     */
    private String type;

    /**
     * 客户端agent
     */
    private String userAgent;

    /**
     * oauth client id
     */
    private String clientId;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 来源的IP
     */
    @TableField("source_ip")
    private String sourceIP;

    /**
     * token
     */
    private String token;

    /**
     * 结果返回码
     */
    private String resultCode;

    /**
     * 结果返回描述
     */
    private String resultDesc;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private Date createTime;

    public static enum LogType {
        WEB_LOGIN("web_login"),WEB_LOGOUT("web_logout");
        String name;

        LogType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
