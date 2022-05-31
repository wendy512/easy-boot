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

package io.github.wendy512.easyboot.mybatisplus.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * mybatis-plus 配置
 * @author wendy512
 * @date 2022-05-24 13:16:45
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "easy-boot.mybatis-plus")
public class MybatisPlusConfiguration {

    /**
     * 租户配置
     */
    private TenantConfiguration tenant = new TenantConfiguration();

    /**
     * 乐观锁
     */
    private OptimisticLockConfiguration optimisticLock = new OptimisticLockConfiguration();


    /**
     * 租户配置
     * @author wendy512
     * @date 2022-05-24 13:16:45
     * @since 1.0.0
     */
    @Data
    public static class TenantConfiguration {

        /**
         * 默认启用
         */
        private boolean enable = false;

        /**
         * 租户字段名
         */
        private String field = "tenant_id";

        /**
         * 需要过滤掉的表名集合
         */
        private Set<String> ignoreTable = new HashSet<>();
    }

    /**
     * 乐观锁配置
     * @author wendy512
     * @date 2022-05-24 13:16:45
     * @since 1.0.0
     */
    @Data
    public static class OptimisticLockConfiguration {

        /**
         * 默认启用
         */
        private boolean enable = false;

        /**
         * 乐观锁字段名
         */
        private String field = "version";

    }
}
