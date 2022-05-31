package io.github.wendy512.easyboot.webx.exception.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.wendy512.easyboot.common.exception.BizException;
import io.github.wendy512.easyboot.vo.ResponseCode;
import io.github.wendy512.easyboot.vo.VoResponse;
import io.github.wendy512.easyboot.webx.exception.translator.ResponseExceptionTranslator;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常统一处理
 * @author taowenwu
 * @date 2021-04-18 12:04:12:04
 * @since 1.0.0
 */
@Component
@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    
    private final ResponseExceptionTranslator exceptionTranslator;

    public ExceptionHandlerAdvice(ResponseExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<VoResponse> handleBizException(BizException e) throws Exception {
        
        if (log.isErrorEnabled()) {
            log.error("", e);
        }
        
        return ResponseEntity.ok(VoResponse.builder().code(e.getCode()).msg(e.getMsg()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<VoResponse> handleException(Exception e) throws Exception {
        
        if (log.isErrorEnabled()) {
            log.error("", e);
        }

        ResponseEntity<VoResponse> response = exceptionTranslator.translate(e);
        //兜底
        if (null == response) {
            response = ResponseEntity.ok(VoResponse.builder().resp(ResponseCode.SYSTEM_ERROR).build());    
        }
       
        return response;
    }
}
