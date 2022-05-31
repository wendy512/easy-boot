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

import redis.clients.jedis.JedisPoolConfig;

/**
 * 连接池配置
 * @author taowenwu
 * @date 2021-04-25 13:07:13:07
 * @since 1.0.0
 */
public class PoolConfig extends JedisPoolConfig {
    /**
     * 当没有空闲连接时，获取一个对象的最大等待时间，默认值60秒
     */
    public static final long DEFAULT_MAX_WAIT_MILLIS = 60 * 1000;

    /**
     * 在检测空闲对象线程检测到对象不需要移除时，是否检测对象的有效性
     */
    public static final boolean DEFAULT_TEST_WHILE_IDLE = true;

    /**
     * 对象最小的空闲时间
     */
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 60 * 1000;

    /**
     * 空闲对象检测线程的执行周期
     */
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60 * 1000;


    public PoolConfig() {
        super();
        setMaxWaitMillis(DEFAULT_MAX_WAIT_MILLIS);
        setTestWhileIdle(DEFAULT_TEST_WHILE_IDLE);
        setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        setTimeBetweenEvictionRunsMillis(DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
    }
}
