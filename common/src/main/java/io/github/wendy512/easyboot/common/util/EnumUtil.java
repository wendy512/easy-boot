package io.github.wendy512.easyboot.common.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author taowenwu
 * @date 2021-04-17 17:40:17:40
 * @since 1.0.0
 */
public class EnumUtil {
    private static Map<Class, Object> CACHE = new ConcurrentHashMap<>();

    /**
     * 根据条件获取枚举对象
     *
     * @param className
     *            枚举类
     * @param predicate
     *            筛选条件
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getEnumObject(Class<T> className, Predicate<T> predicate) {
        if (!className.isEnum()) {
            return null;
        }
        Object obj = CACHE.get(className);
        T[] ts = null;
        if (obj == null) {
            ts = className.getEnumConstants();
            CACHE.put(className, ts);
        } else {
            ts = (T[])obj;
        }

        return Arrays.stream(ts).filter(predicate).findAny();
    }
}
