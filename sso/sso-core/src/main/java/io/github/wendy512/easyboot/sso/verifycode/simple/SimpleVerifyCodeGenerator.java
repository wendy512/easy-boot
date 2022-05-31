package io.github.wendy512.easyboot.sso.verifycode.simple;

import io.github.wendy512.easyboot.sso.verifycode.AbstractVerifyCodeGenerator;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeConfig;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeGenerator;
import io.github.wendy512.easyboot.sso.custom.exception.ValidException;

import java.util.Properties;

/**
 * 数字+字母验证码实现
 * 
 * @author taowenwu
 * @date 2021-04-11 20:36:20:36
 * @since 1.0.0
 */
public class SimpleVerifyCodeGenerator extends AbstractVerifyCodeGenerator implements VerifyCodeGenerator {
    @Override
    public String generateCode() {
        return generateCode(4);
    }

    @Override
    public Properties createProperties(VerifyCodeConfig config) {
        Properties properties = super.createProperties(config);
        //properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        return properties;
    }

    @Override
    public void validate(String text, String input) throws ValidException {
        check(text, input);

        if (!input.toLowerCase().equals(text.toLowerCase())) {
            throw new ValidException("验证码不正确");
        }
    }
}
