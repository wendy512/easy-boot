package io.github.wendy512.easyboot.sso.verifycode.math;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import io.github.wendy512.easyboot.sso.custom.exception.ValidException;
import io.github.wendy512.easyboot.sso.verifycode.AbstractVerifyCodeGenerator;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeConfig;

/**
 * 数学验证码
 * @author taowenwu
 * @date 2021-04-17 17:21:17:21
 * @since 1.0.0
 */
public class MathVerifyCodeGenerator extends AbstractVerifyCodeGenerator {
    private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String generateCode() {
        return getText();
    }

    private String getText(){
        Integer result = 0;
        Random random = new SecureRandom();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder suChinese = new StringBuilder();
        int randomOperands = (int) Math.round(Math.random() * 2);
        if (randomOperands == 0)
        {
            result = x * y;
            suChinese.append(CNUMBERS[x]);
            suChinese.append("*");
            suChinese.append(CNUMBERS[y]);
        }
        else if (randomOperands == 1)
        {
            if (!(x == 0) && y % x == 0)
            {
                result = y / x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("/");
                suChinese.append(CNUMBERS[x]);
            }
            else
            {
                result = x + y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("+");
                suChinese.append(CNUMBERS[y]);
            }
        }
        else if (randomOperands == 2)
        {
            if (x >= y)
            {
                result = x - y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[y]);
            }
            else
            {
                result = y - x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[x]);
            }
        }
        else
        {
            result = x + y;
            suChinese.append(CNUMBERS[x]);
            suChinese.append("+");
            suChinese.append(CNUMBERS[y]);
        }
        suChinese.append("=?@" + result);
        return suChinese.toString();
    }

    @Override
    public BufferedImage generateImage(String text, VerifyCodeConfig verifyCodeConfig) {
        String capStr = text.substring(0, text.lastIndexOf("@"));
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(createProperties(verifyCodeConfig)));
        return kaptcha.createImage(capStr);
    }

    @Override
    public Properties createProperties(VerifyCodeConfig config) {
        Properties properties = super.createProperties(config);
        // 验证码文本字符长度 默认为5
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 干扰实现类
        properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
        //properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");

        return properties;
    }

    @Override
    public void validate(String text, String input) throws ValidException {
        check(text, input);
        String result = text.substring(text.lastIndexOf("@") + 1);
        if (!input.equals(result)) {
            throw new ValidException("验证码不正确");
        }
    }
}
