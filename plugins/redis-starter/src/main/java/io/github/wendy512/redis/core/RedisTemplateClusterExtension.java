package io.github.wendy512.redis.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;

import io.github.wendy512.redis.config.PoolConfig;


/**
 * redis 模板拓展
 * 
 * @author taowenwu
 * @date 2021-04-26 17:41:17:41
 * @since 1.0.0
 */
public class RedisTemplateClusterExtension<K, V> extends RedisTemplate<K, V> {

    private PoolConfig poolConfig;

    private RedisClusterSupportHolder clusterSupportHolder;

    @Override
    protected RedisConnection createRedisConnectionProxy(RedisConnection pm) {
        Class<?>[] ifcs = ClassUtils.getAllInterfacesForClass(pm.getClass(), getClass().getClassLoader());
        InvocationHandler proxy;
        if (pm instanceof RedisClusterConnection) {
            proxy = new CloseSuppressingClusterInvocationHandler(pm, clusterSupportHolder);
        } else {
            proxy = new CloseSuppressingInvocationHandler(pm);
        }
        return (RedisConnection)Proxy.newProxyInstance(pm.getClass().getClassLoader(), ifcs, proxy);
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public RedisClusterSupportHolder getClusterSupportHolder() {
        return clusterSupportHolder;
    }

    public void setClusterSupportHolder(RedisClusterSupportHolder clusterSupportHolder) {
        this.clusterSupportHolder = clusterSupportHolder;
    }
}
