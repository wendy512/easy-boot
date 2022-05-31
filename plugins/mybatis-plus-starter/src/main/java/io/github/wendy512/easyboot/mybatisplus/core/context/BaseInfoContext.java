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

package io.github.wendy512.easyboot.mybatisplus.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import io.github.wendy512.easyboot.mybatisplus.core.dataobject.BaseDO;
import lombok.Data;

/**
 * 获取 {@link BaseDO}相关的属性
 * @author wendy512
 * @date 2022-05-24 18:06:09
 * @since 1.0.0
 */
public final class BaseInfoContext {
    
    private BaseInfoContext() {}

    private static final ThreadLocal<BaseInfo> LOCAL_CACHE = new TransmittableThreadLocal<>(); 
    
    public static void setBaseInfo(BaseInfo baseInfo) {
        LOCAL_CACHE.set(baseInfo);
    }
    
    public static BaseInfo getBaseInfo() {
        return LOCAL_CACHE.get();
    }
    
    @Data
    public static class BaseInfo {
        private String userId;
        private String tenantId;
    }
}
