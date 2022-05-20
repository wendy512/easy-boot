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

package io.github.wendy512.serialization;

/**
 * 序列化接口
 * @author taowenwu
 * @date 2021-05-18 11:11:11:11
 * @since 1.0.0
 */
public interface Serializer {

    <T> byte[] serialize(T t) throws SerializationException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException;

    <T> T deserialize(byte[] bytes) throws SerializationException;
}
