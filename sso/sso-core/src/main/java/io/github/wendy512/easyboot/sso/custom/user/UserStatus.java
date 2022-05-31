package io.github.wendy512.easyboot.sso.custom.user;

/**
 * 用户状态常量类
 * @author taowenwu
 * @date 2021-04-09 14:49:14:49
 * @since 1.0.0
 */
public class UserStatus {

    //1000：有效
    //1001：暂停
    //1010：锁定
    //1011：密码错误锁定
    //1020：停用
    //1100：无效
    //1200：未生效
    //1210：审批通过
    //1220：审批中
    //1230：审批未通过
    //1231：注销
    //1232：离职

    public static final int EFFECT = 1000;
    public static final int PAUSE = 1001;
    public static final int LOCKED = 1010;
    public static final int PASSWORD_LOCKED = 1011;
    public static final int DEACTIVATE = 1020;
    public static final int INVALID = 1100;
    public static final int NOT_EFFECT = 1200;
    public static final int APPROVED = 1210;
    public static final int APPROVAL = 1220;
    public static final int NOT_APPROVAL = 1230;
    public static final int CANCELL = 1231;
    public static final int RESIGN = 1231;
}
