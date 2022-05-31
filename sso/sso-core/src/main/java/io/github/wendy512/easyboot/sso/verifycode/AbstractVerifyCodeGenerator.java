package io.github.wendy512.easyboot.sso.verifycode;

import com.google.code.kaptcha.Constants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

/**
 * 抽象验证码生成器
 * 
 * @author taowenwu
 * @date 2021-04-13 21:40:21:40
 * @since 1.0.0
 */
public abstract class AbstractVerifyCodeGenerator implements VerifyCodeGenerator {

    private static Random random = new Random();
    private static final char[] RANDOM_CODE =  "abcde2345678gfynmnpwx".toCharArray();

    public String generateCode(int length) {
        return getText(length);
    }

    public String getText(int length) {
        Random rand = new SecureRandom();
        StringBuffer text = new StringBuffer();
        for(int i = 0; i < length; ++i) {
            text.append(RANDOM_CODE[rand.nextInt(RANDOM_CODE.length)]);
        }

        return text.toString();
    }

    public Properties createProperties(VerifyCodeConfig config) {
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty(Constants.KAPTCHA_BORDER, "yes");
        // 边框颜色
        properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, "105,179,90");
        // 字体颜色
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        // 图片宽
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, config.getWidth() + "");
        // 图片高
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, config.getHeight() + "");
        // 字体大小
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, config.getFontSize() + "");
        // session key
        properties.setProperty(Constants.KAPTCHA_SESSION_CONFIG_KEY, Constants.KAPTCHA_SESSION_KEY);
        // 字体
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 验证码噪点颜色 默认为Color.BLACK
        properties.setProperty(Constants.KAPTCHA_NOISE_COLOR, "white");
        // 验证码文本字符间距 默认为2
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5");
        properties.setProperty(Constants.KAPTCHA_BORDER, config.isHasBorder() ? "yes" : "no");
        // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
        //properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");

        return properties;
    }

    public BufferedImage generateImage(String text, VerifyCodeConfig verifyCodeConfig) {
        int verifySize = text.length();
        int w = verifyCodeConfig.getWidth();
        int h = verifyCodeConfig.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN,
                Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.YELLOW };
        float[] fractions = new float[colors.length];
        for(int i = 0; i < colors.length; i++){
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);

        if (verifyCodeConfig.isHasBorder()) {
            g2.setColor(Color.GRAY);// 设置边框色
            g2.fillRect(0, 0, w, h);
        }

        Color c = getRandColor(240, 255);
        g2.setColor(Color.WHITE);// 设置背景色
        g2.fillRect(0, 2, w, h-4);

        /*//绘制干扰线
        Random random = new Random();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }*/

        // 添加噪点
        /*float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(0, 0, 0);
        }*/

        shear(g2, w, h, c);// 使图片扭曲

        g2.setColor(new Color(0, 0, 0));
        int fontSize = h-10;
        Font font = new Font("Arial,Courier", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = text.toCharArray();
        for(int i = 0; i < verifySize; i++){
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 8 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1),
                    (w / verifySize) * i + fontSize/2, h/2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((w-10) / verifySize) * i + 5, h/2 + fontSize/2 - 4);
        }

        g2.dispose();
        return image;
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                //g.setColor(color);
                //g.drawLine((int) d, i, 0, i);
                //g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                //g.setColor(color);
                //g.drawLine(i, (int) d, i, 0);
                //g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }
}
