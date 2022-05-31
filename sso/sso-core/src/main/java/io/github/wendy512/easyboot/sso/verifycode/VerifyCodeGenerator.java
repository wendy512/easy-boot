package io.github.wendy512.easyboot.sso.verifycode;

import java.awt.image.BufferedImage;

/**
 * 验证码生成器
 * 
 * @author taowenwu
 * @date 2021-04-11 20:20:20:20
 * @since 1.0.0
 */
public interface VerifyCodeGenerator extends VerifyCodeValidation {

    /**
     * 生成验证码
     * 
     * @return 验证码结果
     */
    String generateCode();

    /**
     * 生成图片
     *
     * @param text 验证码
     * @param verifyCodeConfig
     *            图片基础配置
     * @return 图片文件流
     */
    BufferedImage generateImage(String text, VerifyCodeConfig verifyCodeConfig);
}
