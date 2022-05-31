package io.github.wendy512.easyboot.sso.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeConfig;
import io.github.wendy512.easyboot.sso.verifycode.VerifyCodeGenerator;

/**
 * 验证码控制层
 * 
 * @author taowenwu
 * @date 2021-04-15 20:47:20:47
 * @since 1.0.0
 */
@Controller
@RequestMapping("/verifyCode")
public class VerifyCodeController {

    @Autowired
    private VerifyCodeGenerator verifyCodeGenerator;

    /**
     * 获取验证码，参数：
     * h：高度
     * w：宽度
     * f：字体大小
     * b：是否需要边框，true or false
     * @param params
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/get")
    public void get(@RequestParam Map<String,Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setHeader(response);
        writeImageCode(params, request, response);
    }

    @RequestMapping("/valid")
    public void valid(@RequestParam String input, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String verifyCode = (String) request.getSession().getAttribute(Constants.SESSION_KEY);
        verifyCodeGenerator.validate(verifyCode, input);
    }

    private void writeImageCode(Map<String,Object> params, HttpServletRequest request, HttpServletResponse response) throws IOException {
        VerifyCodeConfig verifyCodeConfig = new VerifyCodeConfig();
        if (MapUtils.isNotEmpty(params)) {
            if (params.containsKey("h")) {
                verifyCodeConfig.setHeight(MapUtils.getInteger(params, "h"));
            }

            if (params.containsKey("w")) {
                verifyCodeConfig.setWidth(MapUtils.getInteger(params, "w"));
            }

            if (params.containsKey("f")) {
                verifyCodeConfig.setFontSize(MapUtils.getInteger(params, "f"));
            }

            if (params.containsKey("b")) {
                verifyCodeConfig.setHasBorder(MapUtils.getBoolean(params, "b"));
            }
        }
        String capText = verifyCodeGenerator.generateCode();// 为图片创建文本
        // 将文本保存在session中。这里就使用包中的静态变量吧
        request.getSession().setAttribute(Constants.SESSION_KEY, capText);
        BufferedImage bi = verifyCodeGenerator.generateImage(capText, verifyCodeConfig); // 创建带有文本的图片
        ServletOutputStream out = response.getOutputStream();
        // 图片数据输出
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    private void setHeader(HttpServletResponse response) {
        response.setDateHeader("Expires", 0);// 禁止server端缓存
        // 设置标准的 HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // 设置IE扩展 HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");// 设置标准 HTTP/1.0 不缓存图片
        response.setContentType("image/jpeg");// 返回一个 jpeg 图片，默认是text/html(输出文档的MIMI类型)
    }
}
