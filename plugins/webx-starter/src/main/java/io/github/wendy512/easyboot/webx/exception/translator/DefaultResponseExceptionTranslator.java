/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.wendy512.easyboot.webx.exception.translator;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.github.wendy512.easyboot.vo.ResponseCode;
import io.github.wendy512.easyboot.vo.VoResponse;
import io.github.wendy512.easyboot.webx.exception.DefaultThrowableAnalyzer;
import io.github.wendy512.easyboot.webx.exception.ThrowableAnalyzer;

/**
 * 默认自定义异常统一转换
 * @author wendy512
 * @date 2022-05-23 10:05:58
 * @since 1.0.0
 */
public class DefaultResponseExceptionTranslator implements ResponseExceptionTranslator {

    protected ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
    
    @Override
    public ResponseEntity<VoResponse> translate(Exception e) {
        ResponseEntity<VoResponse> entity = new ResponseEntity<>(HttpStatus.OK);
        
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
        Exception ex = (NoHandlerFoundException)throwableAnalyzer.getFirstThrowableOfType(NoHandlerFoundException.class, causeChain);
        
        // 404
        String message = e.getMessage();
        if (null != ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(VoResponse.builder().resp(ResponseCode.RESOURCE_NOT_FOUND).msg(message).build());
        }
        
        ex = (HttpRequestMethodNotSupportedException)throwableAnalyzer
            .getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        // 405
        if (null != ex) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(VoResponse.builder().resp(ResponseCode.RESOURCE_NOT_FOUND).msg(message).build());
        }

        ex = (MethodArgumentNotValidException)throwableAnalyzer
            .getFirstThrowableOfType(MethodArgumentNotValidException.class, causeChain);
        if (null != ex) {
            return handleValidException(((MethodArgumentNotValidException)ex).getBindingResult());
        }

        ex = (BindException)throwableAnalyzer
                .getFirstThrowableOfType(BindException.class, causeChain);
        if (null != ex) {
            return handleValidException(((BindException)ex).getBindingResult());
        }

        ex = (ConstraintViolationException)throwableAnalyzer
                .getFirstThrowableOfType(ConstraintViolationException.class, causeChain);
        if (null != ex) {
            return handleValidException(((ConstraintViolationException)ex).getConstraintViolations());
        }

        ex = (HttpMessageNotReadableException)throwableAnalyzer
            .getFirstThrowableOfType(HttpMessageNotReadableException.class, causeChain);
        if (null != ex) {
            return ResponseEntity.ok(VoResponse.builder().resp(ResponseCode.SYSTEM_ERROR).msg("参数格式异常！").build());
        }

        return ResponseEntity.ok(VoResponse.builder().resp(ResponseCode.SYSTEM_ERROR)
            .msg(StringUtils.isBlank(message) ? ResponseCode.SYSTEM_ERROR.getMsg() : message).build());
    }
    
    public ResponseEntity<VoResponse> handleValidException(BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        ObjectError objectError = allErrors.get(0);
        return ResponseEntity.ok(VoResponse.builder().resp(ResponseCode.VALID_ERROR)
            .msg(((FieldError)objectError).getField() + objectError.getDefaultMessage()).build());
    }

    public ResponseEntity<VoResponse> handleValidException(Set<ConstraintViolation<?>> violations) {
        ConstraintViolation violation = violations.iterator().next();
        return ResponseEntity
                .ok(VoResponse.builder().resp(ResponseCode.VALID_ERROR).msg(violation.getMessage()).build());
    }
    
}
