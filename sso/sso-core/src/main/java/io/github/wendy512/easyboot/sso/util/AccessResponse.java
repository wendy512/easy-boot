package io.github.wendy512.easyboot.sso.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.vo.ResponseCode;
import io.github.wendy512.easyboot.vo.VoResponse;

/**
 * @author taowenwu
 * @date 2021-04-03 09:15:9:15
 * @since 1.0.0
 */
public class AccessResponse {

    public static void accessError(HttpServletRequest request, HttpServletResponse response, RuntimeException exception)
        throws IOException, ServletException {
        String errorCode = response.getStatus() + "";
        response.setStatus(HttpStatus.OK.value());

        response.setContentType(Constants.APPLICATION_JSON);
        VoResponse<Object> respData = VoResponse.builder().code(ResponseCode.SYSTEM_ERROR.getCode())
            .msg(exception.getMessage()).build();
        response.getWriter().write(JSON.toJSONString(respData));
    }
}
