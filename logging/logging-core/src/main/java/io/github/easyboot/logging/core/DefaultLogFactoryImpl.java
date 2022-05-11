package io.github.easyboot.logging.core;

import java.lang.reflect.Constructor;

import io.github.easyboot.logging.LogFactory;
import io.github.easyboot.logging.core.slf4j.Slf4jImpl;

/**
 * 默认LogFactory实现，默认使用{@link io.github.easyboot.logging.core.slf4j.Slf4jImpl}
 * @author taowenwu
 * @date 2021-04-01 17:02:17:02
 * @since 1.0.0
 */
public class DefaultLogFactoryImpl extends LogFactory {
    public static final Class LOG_CLASS = Slf4jImpl.class;

    @Override
    public Constructor getConstructor() throws NoSuchMethodException {
        return LOG_CLASS.getConstructor(String.class);
    }
}
