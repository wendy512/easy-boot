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

package io.github.wendy512.serialization.jdk;

import java.io.*;

import io.github.wendy512.serialization.SerializationException;
import io.github.wendy512.serialization.Serializer;

/**
 * jdk序列化
 * 
 * @author taowenwu
 * @date 2021-05-18 14:27:14:27
 * @since 1.0.0
 */
public class JdkSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializationException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] ret;

        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(t);
            ret = bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return ret;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        T instance;

        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            instance = (T) ois.readObject();
        } catch (Exception e) {
            throw new SerializationException(e);
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        return instance;
    }

    @Override
    public <T> T deserialize(byte[] bytes) throws SerializationException {
        return deserialize(bytes, null);
    }

}
