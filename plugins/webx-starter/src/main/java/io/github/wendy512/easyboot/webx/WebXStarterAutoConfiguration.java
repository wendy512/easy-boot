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

package io.github.wendy512.easyboot.webx;

import io.github.wendy512.easyboot.webx.aop.WebXControllerPointcut;
import io.github.wendy512.easyboot.webx.config.WebXConfiguration;
import io.github.wendy512.easyboot.webx.exception.translator.DefaultResponseExceptionTranslator;
import io.github.wendy512.easyboot.webx.exception.translator.ResponseExceptionTranslator;
import io.github.wendy512.easyboot.webx.filter.WebXFilter;
import io.github.wendy512.easyboot.webx.interceptor.WebXInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * spring 自动装配
 * 
 * @author taowenwu
 * @date 2021-04-25 11:38:11:38
 * @since 1.0.0
 */
@ComponentScan("io.github.wendy512.easyboot.webx")
@Configuration
@EnableConfigurationProperties(WebXConfiguration.class)
public class WebXStarterAutoConfiguration implements WebMvcConfigurer {
    
    @ConditionalOnMissingBean(ResponseExceptionTranslator.class)
    @Bean
    public ResponseExceptionTranslator responseExceptionTranslator() {
        return new DefaultResponseExceptionTranslator();
    }
    
    @Bean
    public FilterRegistrationBean<WebXFilter> webXFilter() {
        FilterRegistrationBean<WebXFilter> bean = new FilterRegistrationBean();
        bean.setFilter(new WebXFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*");
        return bean;
    }
    
    @Bean
    public WebXControllerPointcut webXControllerPointcut(WebXConfiguration config) {
        return new WebXControllerPointcut(config);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebXInterceptor());
    }
}
