package io.github.wendy512.easyboot.sso.verifycode;

import io.github.wendy512.easyboot.sso.verifycode.math.MathVerifyCodeGenerator;
import io.github.wendy512.easyboot.sso.verifycode.simple.SimpleVerifyCodeGenerator;

/**
 * 验证码工程创建类
 * 
 * @author taowenwu
 * @date 2021-04-17 17:31:17:31
 * @since 1.0.0
 */
public class VerifyCodeFactory {

    public static VerifyCodeGenerator createGenerator(VerifyCodeType type) {
        if (VerifyCodeType.SIMPLE == type) {
            return new SimpleVerifyCodeGenerator();
        } else if (VerifyCodeType.MATH == type) {
            return new MathVerifyCodeGenerator();
        } else {
            return null;
        }
    }

}
