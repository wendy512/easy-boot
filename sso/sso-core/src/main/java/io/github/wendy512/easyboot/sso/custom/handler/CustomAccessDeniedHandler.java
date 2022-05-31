package io.github.wendy512.easyboot.sso.custom.handler;

import io.github.wendy512.easyboot.sso.util.AccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author taowenwu
 * @date 2021-04-03 08:53:8:53
 * @since 1.0.0
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (log.isErrorEnabled()) {
            log.error("", accessDeniedException);
        }
        AccessResponse.accessError(request, response, accessDeniedException);
    }
}
