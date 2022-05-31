package io.github.wendy512.easyboot.sso.verifycode;

import lombok.AllArgsConstructor;

/**
 * 验证码类型，目前支持：
 * 1. simple 随机数字+字母验证码
 * 2. math 算术题验证码
 * 3. slide 滑动验证码
 * 4. piecing 拼图验证码
 */
@AllArgsConstructor
public enum VerifyCodeType {
    SIMPLE("simple"),MATH("math"),SLIDE("slide"),PIECING("piecing");
    String type;

    public String getType() {
        return type;
    }
}
