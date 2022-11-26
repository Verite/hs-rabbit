/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */

package com.hengdao.hs.rabbit.spring.boot.starter.config;

import com.hengdao.hs.rabbit.spring.boot.starter.props.RedisLockModuleProperties;
import com.hengdao.hs.rabbit.spring.boot.starter.lock.RedisLockClient;
import com.hengdao.hs.rabbit.spring.boot.starter.lock.RedisLockClientImpl;
import jodd.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁自动化配置
 *
 * @author L.cm
 */
@Configuration
@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(RedisLockModuleProperties.class)
@ConditionalOnProperty(value = RedisLockModuleProperties.LOCK_ENABLED, havingValue = "true")
public class RedisLockAutoConfiguration {

    private Config singleConfig(RedisLockModuleProperties properties) {
        Config config = new Config();
        SingleServerConfig serversConfig = config.useSingleServer();
        serversConfig.setAddress(getAddress(properties));
        String password = properties.getPassword();
        if (StringUtil.isNotBlank(password)) {
            serversConfig.setPassword(password);
        }
        serversConfig.setDatabase(properties.getDatabase());
        serversConfig.setConnectionPoolSize(properties.getPoolSize());
        serversConfig.setConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setIdleConnectionTimeout(properties.getConnectionTimeout());
        serversConfig.setConnectTimeout(properties.getConnectionTimeout());
        serversConfig.setTimeout(properties.getTimeout());
        return config;
    }

    private Config masterSlaveConfig(RedisLockModuleProperties properties) {
        Config config = new Config();
        MasterSlaveServersConfig serversConfig = config.useMasterSlaveServers();
        serversConfig.setMasterAddress(properties.getMasterAddress());
        serversConfig.addSlaveAddress(properties.getSlaveAddress());
        String password = properties.getPassword();
        if (StringUtil.isNotBlank(password)) {
            serversConfig.setPassword(password);
        }
        serversConfig.setDatabase(properties.getDatabase());
        serversConfig.setMasterConnectionPoolSize(properties.getPoolSize());
        serversConfig.setMasterConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setSlaveConnectionPoolSize(properties.getPoolSize());
        serversConfig.setSlaveConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setIdleConnectionTimeout(properties.getConnectionTimeout());
        serversConfig.setConnectTimeout(properties.getConnectionTimeout());
        serversConfig.setTimeout(properties.getTimeout());
        return config;
    }

    private Config sentinelConfig(RedisLockModuleProperties properties) {
        Config config = new Config();
        SentinelServersConfig serversConfig = config.useSentinelServers();
        serversConfig.setMasterName(properties.getMasterName());
        serversConfig.addSentinelAddress(properties.getSentinelAddress());
        String password = properties.getPassword();
        if (StringUtil.isNotBlank(password)) {
            serversConfig.setPassword(password);
        }
        serversConfig.setDatabase(properties.getDatabase());
        serversConfig.setMasterConnectionPoolSize(properties.getPoolSize());
        serversConfig.setMasterConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setSlaveConnectionPoolSize(properties.getPoolSize());
        serversConfig.setSlaveConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setIdleConnectionTimeout(properties.getConnectionTimeout());
        serversConfig.setConnectTimeout(properties.getConnectionTimeout());
        serversConfig.setTimeout(properties.getTimeout());
        return config;
    }

    private Config clusterConfig(RedisLockModuleProperties properties) {
        Config config = new Config();
        ClusterServersConfig serversConfig = config.useClusterServers();
        serversConfig.addNodeAddress(properties.getNodeAddress());
        String password = properties.getPassword();
        if (StringUtil.isNotBlank(password)) {
            serversConfig.setPassword(password);
        }
        serversConfig.setMasterConnectionPoolSize(properties.getPoolSize());
        serversConfig.setMasterConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setSlaveConnectionPoolSize(properties.getPoolSize());
        serversConfig.setSlaveConnectionMinimumIdleSize(properties.getIdleSize());
        serversConfig.setIdleConnectionTimeout(properties.getConnectionTimeout());
        serversConfig.setConnectTimeout(properties.getConnectionTimeout());
        serversConfig.setTimeout(properties.getTimeout());
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisLockClient redisLockClient(RedisLockModuleProperties properties) {
        return new RedisLockClientImpl(redissonClient(properties));
    }

    public String getAddress(RedisLockModuleProperties properties) {
        return String.format("redis://%s:%s", properties.getHost(), properties.getPort());
    }

    private RedissonClient redissonClient(RedisLockModuleProperties properties) {
        RedisLockModuleProperties.Mode mode = properties.getMode();
        Config config;
        switch (mode) {
            case sentinel:
                config = sentinelConfig(properties);
                break;
            case cluster:
                config = clusterConfig(properties);
                break;
            case master:
                config = masterSlaveConfig(properties);
                break;
            case single:
                config = singleConfig(properties);
                break;
            default:
                config = new Config();
                break;
        }
        return Redisson.create(config);
    }

}
