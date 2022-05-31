package io.github.wendy512.easyboot.sso.custom.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.vo.VoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * client凭证校验处理类
 * @author taowenwu
 * @date 2021-04-02 19:46:19:46
 * @since 1.0.0
 */
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        
        if (log.isErrorEnabled()) {
            log.error("", exception);
        }
        int errorCode = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(errorCode);

        response.setContentType(Constants.APPLICATION_JSON);
        VoResponse<Object> respData = VoResponse.builder().code(String.valueOf(errorCode)).msg("client凭证错误！").build();
        String json = JSON.toJSONString(respData);
        response.getWriter().write(json);
    }
}
