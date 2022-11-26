package com.hengdao.hs.rabbit.spring.boot.starter.props;

import com.hengdao.hs.rabbit.spring.boot.starter.lock.LockType;
import com.hengdao.hs.rabbit.spring.boot.starter.rabbitMq.RabbitModuleInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * rabbitMq 配置
 * @author Verite
 * @date 2022.11.23
 * @apiNote
 */
@Data
@ConfigurationProperties(RabbitModuleProperties.PREFIX)
public class RabbitModuleProperties {


    /**
     * 配置前置字段
     */
    public static final String PREFIX = "spring.rabbitmq";

    /**
     * 单机配置：rabbit 主机
     */
    private String host ;

     /**
     * 单机配置：rabbit 端口
     */
    private int port ;
    /**
     * 用户名称
     */
    private String username;
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

    private List<RabbitModuleInfo> modules;
}
