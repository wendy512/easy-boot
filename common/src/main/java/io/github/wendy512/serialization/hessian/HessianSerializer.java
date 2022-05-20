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

package io.github.wendy512.serialization.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;

/**
 * hessian 序列化
 * @author taowenwu
 * @date 2021-05-18 11:45:11:45
 * @since 1.0.0
 */
public class HessianSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        HessianOutput opt = null;
        byte[] ret;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            opt = new HessianOutput(os);
            opt.writeObject(t);
            opt.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                if (null != opt) opt.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        T instance;
        HessianInput input = null;

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            input = new HessianInput(is);
            if (null != clazz) {
                return (T) input.readObject(clazz);
            } else {
                return (T) input.readObject();
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            if (null != input) input.close();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        return deserialize(bytes, null);
    }

}
