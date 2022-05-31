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

package io.github.wendy512.easyboot.redis.config;

import java.util.List;

import lombok.Data;

/**
 * redis 实例配置
 * 
 * @author taowenwu
 * @date 2021-04-25 14:06:14:06
 * @since 1.0.0
 */
@Data
public class InstanceConfig {
    public InstanceConfig() {}

    public InstanceConfig(String host, int port, String password, long timeout) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
    }

    private String host = "localhost";
    private int port = 6379;
    private Integer database;
    private String password;
    private long timeout = 5000;
    private String keySerializer = "org.springframework.data.redis.serializer.StringRedisSerializer";
    private String valueSerializer = "io.github.wendy512.easyboot.redis.serializer.string.StringExtensionRedisSerializer";
    private String hashKeySerializer = "org.springframework.data.redis.serializer.StringRedisSerializer";
    private String hashValueSerializer = "io.github.wendy512.easyboot.redis.serializer.string.StringExtensionRedisSerializer";
    private String master;
    /**
     * 哨兵模式
     */
    private boolean enableSentinel = false;
    /**
     * 是否开启隐射容器中的节点IP和Port
     */
    private boolean enableNodeMapping = false;

    /**
     * 数据压缩配置
     */
    private CompressConfig compressConfig;

    /**
     * 节点列表
     */
    private List<NodeConfig> nodes;

    /**
     * 集群模式
     */
    private boolean enableCluster = false;

}
