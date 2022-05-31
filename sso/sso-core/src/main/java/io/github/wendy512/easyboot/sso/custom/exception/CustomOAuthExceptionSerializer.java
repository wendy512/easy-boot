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

package io.github.wendy512.easyboot.sso.custom.exception;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.wendy512.easyboot.vo.VoResponse;

/**
 * 自定义序列OAuthException
 * @author taowenwu
 * @date 2021-04-09 20:04:20:04
 * @since 1.0.0
 */
public class CustomOAuthExceptionSerializer extends StdSerializer<CustomOAuthException> {
    public CustomOAuthExceptionSerializer() {
        super(CustomOAuthException.class);
    }

    @Override
    public void serialize(CustomOAuthException value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        gen.writeStartObject();
        VoResponse<Object> response =
            VoResponse.builder().code(String.valueOf(value.getHttpErrorCode())).msg(value.getMessage()).build();
        gen.writeObject(response);
        gen.writeEndObject();
    }
}
