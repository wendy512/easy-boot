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

package io.github.wendy512.easyboot.redis.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.github.wendy512.easyboot.redis.core.RedisClusterSupportHolder;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * 支持cluster
 * @author taowenwu
 * @date 2021-04-27 14:09:14:09
 * @since 1.0.0
 */
@Slf4j
public class CloseSuppressingClusterInvocationHandler implements InvocationHandler {
    private static final String CLOSE = "close";
    private static final String HASH_CODE = "hashCode";
    private static final String EQUALS = "equals";
    private static final String TO_STRING = "toString";
    private static final String OPEN_PIPELINE = "openPipeline";
    private static final String CLOSE_PIPELINE = "closePipeline";
    private static final Set<String> PARAM_KEY_NAMES = new HashSet<>(Arrays.asList("key", "keys"));

    private final Object target;
    private RedisClusterSupportHolder redisClusterSupportHolder;

    public CloseSuppressingClusterInvocationHandler(Object target, RedisClusterSupportHolder redisClusterSupportHolder) {
        this.target = target;
        this.redisClusterSupportHolder = redisClusterSupportHolder;
    }

    @Override
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals(EQUALS)) {
            // Only consider equal when proxies are identical.
            return (proxy == args[0]);
        } else if (method.getName().equals(HASH_CODE)) {
            // Use hashCode of PersistenceManager proxy.
            return System.identityHashCode(proxy);
        } else if (method.getName().equals(CLOSE)) {
            // Handle close method: suppress, not valid.
            return null;
        } else if (method.getName().equals(OPEN_PIPELINE)) {
            redisClusterSupportHolder.openAllPipeline();
            return null;
        } else if (method.getName().equals(CLOSE_PIPELINE)) {
            return redisClusterSupportHolder.closeAllPipeline();
        }

        // Invoke method on target RedisConnection.
        try {
            int keyIndex = getKey(method);
            if (keyIndex != -1) {
                Object arg = args[keyIndex];
                return method.invoke(redisClusterSupportHolder.getSlotConnection((byte[]) arg), args);
            } else {
                if (!method.getName().equals(TO_STRING)) {
                    log.error("Current method [{}] not support pipeline, Please be careful, Args: {}", method.getName(),
                            args);
                }
                return method.invoke(target, args);
            }
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }

    }

    private int getKey(Method method) {
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter p = parameters[i];
            if (PARAM_KEY_NAMES.contains(parameterNames[i]) && ("[B".equals(p.getType().getName()))) {
                return i;
            }
        }

        return -1;
    }
}
