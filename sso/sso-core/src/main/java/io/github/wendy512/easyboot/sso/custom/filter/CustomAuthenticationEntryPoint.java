package io.github.wendy512.easyboot.sso.custom.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.easyboot.sso.constants.Constants;
import io.github.wendy512.easyboot.webx.exception.translator.ResponseExceptionTranslator;


/**
 * 资源无法访问异常
 * @author taowenwu
 * @date 2021-04-02 21:36:21:36
 * @since 1.0.0
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private ResponseExceptionTranslator exceptionTranslator;
    
    public CustomAuthenticationEntryPoint(ResponseExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity responseEntity = exceptionTranslator.translate(authException);
        response.setStatus(responseEntity.getStatusCode().value());

        Set<Map.Entry<String, List<String>>> entries = responseEntity.getHeaders().entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            response.setHeader(entry.getKey(), String.join(";", entry.getValue()));
        }
        response.setContentType(Constants.APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(responseEntity.getBody()));
    }
}
