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

package io.github.wendy512.easyboot.mybatisplus.core.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import io.github.wendy512.easyboot.mybatisplus.core.context.BaseInfoContext;
import io.github.wendy512.easyboot.mybatisplus.config.MybatisPlusConfiguration;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

/**
 * 租户数据处理
 * @author wendy512
 * @date 2022-05-24 17:06:58
 * @since 1.0.0
 */
public class DefaultTenantLineHandler implements TenantLineHandler {
    
    private final MybatisPlusConfiguration config;
    private final BaseInfoFillHandler baseInfoFillHandler;

    public DefaultTenantLineHandler(MybatisPlusConfiguration config, BaseInfoFillHandler baseInfoFillHandler) {
        this.config = config;
        this.baseInfoFillHandler = baseInfoFillHandler;
    }

    @Override
    public Expression getTenantId() {
        BaseInfoContext.BaseInfo baseInfo = baseInfoFillHandler.getBaseInfo();
        if (null != baseInfo) {
            return new StringValue(baseInfo.getTenantId());
        }
        return new StringValue(null);
    }

    @Override
    public String getTenantIdColumn() {
        return config.getTenant().getField();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return config.getTenant().getIgnoreTable().contains(tableName);
    }
}
