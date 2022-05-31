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

import java.util.Date;
import java.util.Objects;

import io.github.wendy512.easyboot.mybatisplus.core.context.BaseInfoContext;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import io.github.wendy512.easyboot.mybatisplus.core.dataobject.BaseDO;

/**
 * 对{@link BaseDO}的字段进行填充
 * @author wendy512
 * @date 2022-05-24 13:46:28
 * @since 1.0.0
 */
public class BaseDOFillHandler implements MetaObjectHandler {
    
    private final BaseInfoFillHandler baseInfoFillHandler;

    public BaseDOFillHandler(BaseInfoFillHandler baseInfoFillHandler) {
        this.baseInfoFillHandler = baseInfoFillHandler;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO) {
            
            BaseDO baseDO = (BaseDO) metaObject.getOriginalObject();
            Date current = new Date();
            BaseInfoContext.BaseInfo baseInfo = baseInfoFillHandler.getBaseInfo();

            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            
            if (Objects.isNull(baseDO.getModifyTime())) {
                baseDO.setModifyTime(current);
            }

            if (null != baseInfo && Objects.isNull(baseDO.getCreateUser())) {
                baseDO.setCreateUser(baseInfo.getUserId());
            }
            
            if (null != baseInfo && Objects.isNull(baseDO.getModifyUser())) {
                baseDO.setModifyUser(baseInfo.getUserId());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO) {
            
            BaseDO baseDO = (BaseDO) metaObject.getOriginalObject();
            Date current = new Date();
            BaseInfoContext.BaseInfo baseInfo = baseInfoFillHandler.getBaseInfo();
            
            if (Objects.isNull(baseDO.getModifyTime())) {
                baseDO.setModifyTime(current);
            }
            
            if (null != baseInfo && Objects.isNull(baseDO.getModifyUser())) {
                baseDO.setModifyUser(baseInfo.getUserId());
            }
        }
    }
}
