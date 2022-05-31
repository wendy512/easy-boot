package io.github.wendy512.easyboot.sso.verifycode;

import io.github.wendy512.easyboot.sso.custom.exception.ValidException;
import org.apache.commons.lang3.StringUtils;

/**
 * 验证码校验器
 * 
 * @author taowenwu
 * @date 2021-04-11 20:21:20:21
 * @since 1.0.0
 */
public interface VerifyCodeValidation {
    void validate(String text, String input) throws ValidException;

    default void check(String text, String input) throws ValidException {
        if (StringUtils.isBlank(text)) {
            throw new ValidException("请重新生成验证码");
        }

        if (StringUtils.isBlank(input)) {
            throw new ValidException("验证码不能为空");
        }
    }

}
