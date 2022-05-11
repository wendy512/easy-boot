package io.github.wendy512.redis.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.github.wendy512.redis.RedisConnectionFactoryBuilder;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import io.github.wendy512.redis.RedisMode;
import io.github.wendy512.redis.config.InstanceConfig;
import io.github.wendy512.redis.config.PoolConfig;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.util.JedisClusterCRC16;

/**
 * redis 集群管理
 * 
 * @author taowenwu
 * @date 2021-04-30 09:26:9:26
 * @since 1.0.0
 */
@Slf4j
public class RedisClusterSupportHolder {
    private RedisConnectionFactory connectionFactory;
    private long timeout;
    private String password;
    private PoolConfig poolConfig;
    private Map<String, RedisClusterNodeExtension> clusterNodes = new ConcurrentHashMap<>();
    private Map<String, RedisClusterNodeExtension> masterNodes = new ConcurrentHashMap<>();

    public RedisClusterSupportHolder(RedisConnectionFactory connectionFactory, PoolConfig poolConfig) {
        this.connectionFactory = connectionFactory;
        this.poolConfig = poolConfig;
        if (connectionFactory instanceof JedisConnectionFactory) {
            JedisConnectionFactory factory = (JedisConnectionFactory)connectionFactory;
            this.password = factory.getPassword();
            this.timeout = factory.getTimeout();
        } else if (connectionFactory instanceof LettuceConnectionFactory) {
            LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory)connectionFactory;
            this.password = (lettuceConnectionFactory).getPassword();
            this.timeout = (int)(lettuceConnectionFactory).getTimeout();
        }

        init();
    }

    private void init() {
        Iterable<RedisClusterNode> redisClusterNodes = connectionFactory.getClusterConnection().clusterGetNodes();
        redisClusterNodes.forEach(node -> {
            RedisConnectionFactory connectionFactory = getConnectionFactory(node);
            RedisClusterNodeExtension nodeExtension =
                    new RedisClusterNodeExtension(node, connectionFactory, connectionFactory.getConnection());
            if (node.isMaster()) {
                masterNodes.put(node.getId(), nodeExtension);
            }

            clusterNodes.put(node.getId(), nodeExtension);
        });

        new RedisClusterMonitor().start();
    }

    private RedisConnectionFactory getConnectionFactory(RedisClusterNode node) {
        InstanceConfig instanceConfig = new InstanceConfig(node.getHost(), node.getPort(), password, timeout);
        return RedisConnectionFactoryBuilder.builder().mode(RedisMode.SINGLE).instanceConfig(instanceConfig)
            .poolConfig(poolConfig).build();
    }

    public void openAllPipeline() {
        masterNodes.values().forEach(node -> node.getConnection().openPipeline());
    }

    public List<Object> closeAllPipeline() {
        final List<Object> result = new ArrayList<>();
        masterNodes.values().forEach(node -> result.addAll(node.getConnection().closePipeline()));
        return result;
    }

    public List<RedisConnection> getAllConnection() {
        return masterNodes.values().stream().map(RedisClusterNodeExtension::getConnection).collect(Collectors.toList());
    }

    public RedisConnection getSlotConnection(byte[] key) {
        int slot = JedisClusterCRC16.getSlot(key);
        for (RedisClusterNodeExtension node : masterNodes.values()) {
            if (node.getNode().getSlotRange().contains(slot)) {
                return node.getConnection();
            }
        }

        return null;
    }

    public void closeAllConnection() {
        masterNodes.values()
            .forEach(node -> RedisConnectionUtils.releaseConnection(node.getConnection(), node.getConnectionFactory()));
    }

    private class RedisClusterMonitor extends Thread {
        @Override
        public void run() {
            log.info("starting monitor redis cluster node.........");
            Timer timer = new Timer("redis-cluster-monitor");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    monitor();
                }
            }, 0, 2000);
        }

        /**
         * redis 监控并且保证pipeline的connection重置正常
         */
        private void monitor() {
            Iterable<RedisClusterNode> redisClusterNodes = connectionFactory.getClusterConnection().clusterGetNodes();
            StringBuilder info = new StringBuilder();
            redisClusterNodes.forEach(node -> {
                String id = node.getId();
                info.append("host: ").append(node.getHost()).append(", port: ").append(node.getPort())
                    .append(", role: ").append(node.getFlags()).append(", linkState: ").append(node.getLinkState())
                    .append(", connected: ").append(node.isConnected()).append("\n");
                //master变成了slave
                if (node.isSlave() && node.isConnected() && masterNodes.containsKey(node.getId())) {
                    log.error("node {} change status to slave, host: {}, port: {}", id, node.getHost(), node.getPort());
                    resetNode(node, id);
                    masterNodes.remove(id);
                }

                //slave变成了master
                if (node.isMaster() && node.isConnected() && !masterNodes.containsKey(id)) {
                    log.error("node {} change status to master, host: {}, port: {}", id, node.getHost(), node.getPort());
                    RedisClusterNodeExtension nodeExtension = clusterNodes.get(id);
                    resetNode(node, id);
                    masterNodes.put(id, nodeExtension);
                }

                //node 挂掉了
                if (!node.isConnected()) {
                    log.error("node {} is down, host: {}, port: {}", id, node.getHost(), node.getPort());
                    resetNode(node, id);
                    if (masterNodes.containsKey(id)) {
                        masterNodes.remove(id);
                    }
                }
            });

            log.debug(info.toString());
        }

        private void resetNode(RedisClusterNode node, String id) {
            RedisClusterNodeExtension nodeExtension = clusterNodes.get(id);
            nodeExtension.setNode(node);
        }
    }
}
