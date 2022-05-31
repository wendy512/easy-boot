/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.wendy512.easyboot.webx.aop;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;

import io.github.wendy512.easyboot.logging.Log;
import io.github.wendy512.easyboot.logging.LogFactory;
import io.github.wendy512.easyboot.vo.VoResponse;
import io.github.wendy512.easyboot.vo.VoResponse.DurationContext;
import io.github.wendy512.easyboot.webx.config.WebXConfiguration;

/**
 * 拦截controller 方法
 * 
 * @author wendy512
 * @date 2022-05-23 15:36:58
 * @since 1.0.0
 */
@Aspect
public class WebXControllerPointcut {

    private static final Log log = LogFactory.getLog("io.github.wendy512.WebX");
    private final WebXConfiguration config;

    public WebXControllerPointcut(WebXConfiguration config) {
        this.config = config;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) ||"
        + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
        + "@annotation(org.springframework.web.bind.annotation.GetMapping) ||"
        + "@annotation(org.springframework.web.bind.annotation.PutMapping) ||"
        + "@annotation(org.springframework.web.bind.annotation.DeleteMapping) ||"
        + "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint jp) {
        initDuration();
        printRequest(jp);
    }

    /**
     * 打印请求参数
     * 
     * @param jp
     */
    private void printRequest(JoinPoint jp) {
        if (!config.getPrint().isEnablePrintRequest()) {
            return;
        }

        String[] argNames = ((MethodSignature)jp.getSignature()).getParameterNames();
        Object[] argValues = jp.getArgs();

        // 拼接参数
        Map<String, Object> args = new HashMap<>(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            // 被忽略时，标记为 ignore 字符串，避免和 null 混在一起
            args.put(argName, !isIgnoreArgs(argValue) ? argValue : "[ignore]");
        }

        if (log.isInfoEnabled() && !args.isEmpty()) {
            log.info("WebX print request: {}", JSON.toJSONString(args));
        }
    }

    private void initDuration() {
        // 记录开始时间
        DurationContext.start(System.currentTimeMillis());
    }

    @AfterReturning(value = "pointCut()", returning = "response")
    public void after(JoinPoint jp, Object response) {
        resetDuration(response);
        printRequest(response);
    }

    private void resetDuration(Object response) {
        long end = System.currentTimeMillis();

        if (response instanceof VoResponse) {

            Map<String, Object> extra = ((VoResponse)response).getExtra();
            // 重新计算时间
            if (null != extra) {
                extra.put("end", end);
                extra.put("duration", (end - DurationContext.start()));
            }
        }

        // 记录结束时间
        DurationContext.end(end);
    }

    /**
     * 打印响应参数
     * 
     * @param response
     */
    private void printRequest(Object response) {
        if (!config.getPrint().isEnablePrintResponse()) {
            return;
        }

        if (log.isInfoEnabled() && null != response) {
            log.info("WebX print response: {}", JSON.toJSONString(response));
        }
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        // 即使异常也记录结束时间
        DurationContext.end(System.currentTimeMillis());
    }

    private static boolean isIgnoreArgs(Object object) {
        Class<?> clazz = object.getClass();
        // 处理数组的情况
        if (clazz.isArray()) {
            return IntStream.range(0, Array.getLength(object))
                .anyMatch(index -> isIgnoreArgs(Array.get(object, index)));
        }
        
        // 递归，处理数组、Collection、Map 的情况
        if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>)object).stream().anyMatch((Predicate<Object>)WebXControllerPointcut::isIgnoreArgs);
        }
        
        if (Map.class.isAssignableFrom(clazz)) {
            return isIgnoreArgs(((Map<?, ?>)object).values());
        }
        
        // obj
        return object instanceof MultipartFile || object instanceof HttpServletRequest
            || object instanceof HttpServletResponse || object instanceof BindingResult;
    }
}
