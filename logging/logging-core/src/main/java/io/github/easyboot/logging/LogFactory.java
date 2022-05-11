package io.github.easyboot.logging;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import io.github.easyboot.logging.core.LogException;

/**
 * Log工厂
 * 
 * @author taowenwu
 * @date 2021-04-01 15:57:15:57
 * @since 1.0.0
 */
public abstract class LogFactory {
    private static Constructor logConstructor;

    public abstract Constructor getConstructor() throws NoSuchMethodException;

    public static Log getLog(String clazz) {
        try {
            return (Log)logConstructor.newInstance(clazz);
        } catch (Exception e) {
            throw new LogException((new StringBuilder()).append("Error creating logger for logger ").append(clazz)
                .append(".  Cause: ").append(e).toString(), e);
        }
    }

    public static Log getLog(Class clazz) {
        return getLog(clazz.getName());
    }

    static {
        loadSPI();
    }

    /**
     * 通过SPI方式来加载
     */
    private static void loadSPI() {
        ServiceLoader<LogFactory> loaders = ServiceLoader.load(LogFactory.class);
        Iterator<LogFactory> it = loaders.iterator();
        List<LogFactory> implement = new ArrayList<>();
        while (it.hasNext()) {
            implement.add(it.next());
        }

        if (implement.isEmpty()) {
            return;
        }
        try {
            // 拿到最后一个实现
            LogFactory logFactory = implement.get(implement.size() - 1);
            logConstructor = logFactory.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new LogException("Error creating log error", e);
        }

    }

}
