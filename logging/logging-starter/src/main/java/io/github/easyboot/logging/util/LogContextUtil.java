package io.github.easyboot.logging.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

/**
 * logger 监听器
 * @author wendy512
 * @date 2022-05-08 17:25:17:25
 * @since 1.0.0
 */
public class LogContextUtil extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private static Context context;
    private volatile boolean started = false;

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }
    
    public static Context getLoggerContext() {
        return context;
    }

    @Override
    public void start() {
        if (started) {
            return;
        }
        context = getContext();
        started = true;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
