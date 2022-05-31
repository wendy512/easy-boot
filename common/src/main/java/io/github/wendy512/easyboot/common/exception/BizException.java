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

package io.github.wendy512.easyboot.common.exception;

import lombok.Getter;

/**
 * 业务异常的统一对象
 * 
 * @author wendy512
 * @date 2022-05-21 14:26:05
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {
    
    public static final String DEFAULT_BIZ_ERROR_CODE = "500";
    public static final String DEFAULT_BIZ_ERROR_MSG = "系统错误";
    private final ErrorCode errorCode;

    public BizException(String msg) {
        this(DEFAULT_BIZ_ERROR_CODE, msg);
    }

    public BizException(String code, String msg) {
        super(String.format("business error, code=%s, msg=%s", code, msg));
        this.errorCode = new ErrorCode(DEFAULT_BIZ_ERROR_CODE, msg);
    }

    public BizException(String code, String msg, Throwable cause) {
        super(String.format("business error, code=%s, msg=%s", code, msg), cause);
        this.errorCode = new ErrorCode(DEFAULT_BIZ_ERROR_CODE, msg);
    }

    public BizException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public BizException(Throwable cause) {
        this(DEFAULT_BIZ_ERROR_CODE, DEFAULT_BIZ_ERROR_MSG);
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public String getMsg() {
        return errorCode.getMsg();
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
