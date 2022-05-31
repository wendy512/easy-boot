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

package io.github.wendy512.easyboot.vo;

import java.util.Map;

/**
 * 统一错误码
 * 
 * @author wendy512
 * @date 2022-05-23 09:33:24
 * @since 1.0.0
 */
public enum ResponseCode {
    SUCCESS("200", "成功"), AUTH_ERROR("400", "鉴权错误"), AUTH_ERROR_USER_PASSWORD("401", "用户名或者密码错误"),
    AUTH_ERROR_VERIFY_CODE("402", "验证码不正确"), AUTH_LOGOUT_ERROR("403", "登出失败，请重试！"),
    RESOURCE_NOT_FOUND("404", "资源访问不可达！"), SYSTEM_ERROR("500", "系统错误，请稍后重试！"), VALID_ERROR("400", "请求参数错误！");

    private String code;
    private String msg;

    ResponseCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    
    public static boolean isSuccess(String code) {
        return SUCCESS.getCode().equals(code);
    }

    public static boolean isSuccess(Map<String,Object> response) {
        if (null != response && !response.isEmpty()) {
            return SUCCESS.getCode().equals(response.get(VoResponse.CODE_KEY));
        }
        return false;
    }
}
