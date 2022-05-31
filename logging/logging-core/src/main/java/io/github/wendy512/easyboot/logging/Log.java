package io.github.wendy512.easyboot.logging;

/**
 * 日志接口
 * @author taowenwu
 * @date 2021-04-01 15:49:15:49
 * @since 1.0.0
 */
public interface Log {

    /**
     * debug日志
     *
     * @param message 日志内容
     */
    void debug(String message);

    /**
     * debug日志
     *
     * @param message 日志内容
     * @param t 异常
     */
    void debug(String message, Throwable t);

    /**
     * debug日志
     *
     * @param message 日志内容
     * @param arguments 占位符参数
     */
    void debug(String message, Object... arguments);

    /**
     * error日志
     *
     * @param message 日志内容
     */
    void error(String message);

    /**
     * error日志
     *
     * @param message 日志内容
     * @param t 异常
     */
    void error(String message, Throwable t);

    /**
     * error日志
     *
     * @param message 日志内容
     * @param arguments 占位符参数
     */
    void error(String message, Object... arguments);

    /**
     * info日志
     *
     * @param message 日志内容
     */
    void info(String message);

    /**
     * info日志
     *
     * @param message 日志内容
     * @param t 异常
     */
    void info(String message, Throwable t);

    /**
     * info日志
     *
     * @param message 日志内容
     * @param arguments 占位符参数
     */
    void info(String message, Object... arguments);

    /**
     * debug级别是否启用
     *
     * @return 如果配置中启用了，则返回true
     */
    boolean isDebugEnabled();

    /**
     * error级别是否启用
     *
     * @return 如果配置中启用了，则返回true
     */
    boolean isErrorEnabled();

    /**
     * info级别是否启用
     *
     * @return 如果配置中启用了，则返回true
     */
    boolean isInfoEnabled();

    /**
     * trace级别是否启用
     *
     * @return 如果配置中启用了，则返回true
     */
    boolean isTraceEnabled();

    /**
     * warn级别是否启用
     *
     * @return 如果配置中启用了，则返回true
     */
    boolean isWarnEnabled();

    /**
     * trace日志
     *
     * @param message 日志内容
     */
    void trace(String message);

    /**
     * trace日志
     *
     * @param message 日志内容
     * @param t 异常
     */
    void trace(String message, Throwable t);

    /**
     * trace日志
     *
     * @param message 日志内容
     * @param arguments 占位符参数
     */
    void trace(String message, Object... arguments);

    /**
     * warn日志
     *
     * @param message 日志内容
     */
    void warn(String message);

    /**
     * warn日志
     *
     * @param message 日志内容
     * @param t 异常
     */
    void warn(String message, Throwable t);

    /**
     * warn日志
     *
     * @param message 日志内容
     * @param arguments 占位符参数
     */
    void warn(String message, Object... arguments);
}
