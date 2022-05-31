package io.github.wendy512.easyboot.sso.verifycode;

import lombok.Data;

/**
 * 验证码配置
 * @author taowenwu
 * @date 2021-04-15 20:21:20:21
 * @since 1.0.0
 */
@Data
public class VerifyCodeConfig {
    private int width = 100;
    private int height = 50;
    private boolean hasBorder = false;
    private int fontSize = 35;
}
