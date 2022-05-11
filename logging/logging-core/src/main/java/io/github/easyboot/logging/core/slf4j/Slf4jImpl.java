package io.github.easyboot.logging.core.slf4j;

import io.github.easyboot.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * slf4j实现
 * 
 * @author taowenwu
 * @date 2021-04-01 16:18:16:18
 * @since 1.0.0
 */
public class Slf4jImpl implements Log {
    private Logger logger;

    public Slf4jImpl(String clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public Slf4jImpl(Class clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void debug(String message) {
        this.logger.debug(message);
    }

    public void debug(String message, Throwable t) {
        this.logger.debug(message, t);
    }

    @Override
    public void debug(String message, Object... arguments) {
        this.logger.debug(message, arguments);
    }

    public void error(String message) {
        this.logger.error(message);
    }

    public void error(String message, Throwable t) {
        this.logger.error(message, t);
    }

    @Override
    public void error(String message, Object... arguments) {
        this.logger.error(message, arguments);
    }

    public void info(String message) {
        this.logger.info(message);
    }

    public void info(String message, Throwable t) {
        this.logger.info(message, t);
    }

    @Override
    public void info(String message, Object... arguments) {
        this.logger.info(message, arguments);
    }

    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    public void trace(String message) {
        this.logger.trace(message);
    }

    public void trace(String message, Throwable t) {
        this.logger.trace(message, t);
    }

    @Override
    public void trace(String message, Object... arguments) {
        this.logger.trace(message, arguments);
    }

    public void warn(String message) {
        this.logger.warn(message);
    }

    public void warn(String message, Throwable t) {
        this.logger.warn(message, t);
    }

    @Override
    public void warn(String message, Object... arguments) {
        this.logger.warn(message, arguments);
    }
}
