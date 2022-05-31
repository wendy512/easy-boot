package io.github.wendy512.easyboot.sso.custom.exception;

/**
 * 校验exception
 * 
 * @author taowenwu
 * @date 2021-04-17 18:55:18:55
 * @since 1.0.0
 */
public class ValidException extends RuntimeException {
    private String message;

    public ValidException(String message) {
        super(message);
    }
}
