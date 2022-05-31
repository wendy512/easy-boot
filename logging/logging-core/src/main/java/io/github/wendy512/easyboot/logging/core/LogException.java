package io.github.wendy512.easyboot.logging.core;

/**
 * @author taowenwu
 * @date 2021-04-01 16:25:16:25
 * @since 1.0.0
 */
public class LogException extends RuntimeException {
    private static final long serialVersionUID = -2248816093367340175L;

    public LogException() {}

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}
