package io.github.wendy512.redis.config;

import lombok.Data;

/**
 * 节点配置
 * @author taowenwu
 * @date 2021-04-25 14:34:14:34
 * @since 1.0.0
 */
@Data
public class NodeConfig {
    private String node;
    /**
     * 容器中的节点IP和Port，为了解决获取集群节点时IP隐射问题
     */
    private String containerNode;
}
