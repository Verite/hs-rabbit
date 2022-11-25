package com.hengdao.hs.rabbit.spring.boot.starter.config;

import com.hengdao.hs.rabbit.spring.boot.starter.lock.LockType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * rabbitMq 配置
 * @author Verite
 * @date 2022.11.23
 * @apiNote
 */
@Getter
@Setter
@Component
@ConfigurationProperties(RabbitProperties.PREFIX)
public class RabbitProperties {
    /**
     * 配置前置字段
     */
    public static final String PREFIX = "spring.rabbit";
    /**
     * 是否开启redis存储字段名称。
     */
    public static final String REDIS_CACHE_ENABLED = PREFIX + ".redisCacheEnable";
    /**
     * 是否开启：默认为：false，便于生成配置提示。
     */
    private Boolean redisCacheEnabled = false;
    /**
     * 单机配置：rabbit 主机
     */
    private String host = "localhost";
    /**
     * 单机配置：rabbit 端口
     */
    private int port = 5672;
    /**
     * 用户名称
     */
    private String username = "guest";
    /**
     * 用户组
     */
    private final String virtualHost="/histo-customer";
    /**
     * 用户密码
     */
    private String password = "guest";
    /**
     * redis 锁 等待时间
     */
    private Integer waitOut = 30;
    /**
     * redis 锁 最多使用时间
     */
    private Integer leaseTime = 10;
    /**
     * 验签标志
     */
    private String secret = "histo";
    /**
     * redis 锁 单位
     */
    private final TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 验签标志 appId
     */
    private String appid = "histo";
    /**
     * redis 锁标志
     */
    private LockType lockType = LockType.FAIR;
}
