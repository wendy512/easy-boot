package io.github.wendy512.easyboot.sso.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * spring 上下文util
 * 
 * @author taowenwu
 * @date 2021-04-17 11:34:11:34
 * @since 1.0.0
 */
@Component
@Order(Integer.MIN_VALUE)
public class SpringContextUtil implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory context;


    public static ConfigurableListableBeanFactory getContext() {
        return context;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        context = beanFactory;
    }
}
