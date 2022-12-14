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

package com.hengdao.hs.rabbit.spring.boot.starter.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 分布式锁配置
 *
 * @author L.cm
 */
@Getter
@Setter
@ConfigurationProperties(RedisLockModuleProperties.PREFIX)
public class RedisLockModuleProperties {
	/**
	 * 配置前置字段
	 */
	public static final String PREFIX = "spring.redis";
	/**
	 * 是否开启配置项名称。
	 */
	public static final String LOCK_ENABLED = PREFIX + ".enabled";

	/**
	 * 是否开启：默认为：false，便于生成配置提示。
	 */
	private Boolean enabled = Boolean.TRUE;
	/**
	 * 单机配置：redis 服务地址
	 */
	private String host = "127.0.0.1";
	/**
	 * 单机配置：redis 端口
	 */
	private Integer port = 6379;
	/**
	 * 密码配置
	 */
	private String password = "123456";
	/**
	 * db
	 */
	private Integer database = 0;
	/**
	 * 连接池大小
	 */
	private Integer poolSize = 20;
	/**
	 * 最小空闲连接数
	 */
	private Integer idleSize = 5;
	/**
	 * 连接空闲超时，单位：毫秒
	 */
	private Integer idleTimeout = 60000;
	/**
	 * 连接超时，单位：毫秒
	 */
	private Integer connectionTimeout = 3000;
	/**
	 * 命令等待超时，单位：毫秒
	 */
	private Integer timeout = 10000;
	/**
	 * 集群模式，单机：single，主从：master，哨兵模式：sentinel，集群模式：cluster
	 */
	private Mode mode = Mode.single;
	/**
	 * 主从模式，主地址
	 */
	private String masterAddress;
	/**
	 * 主从模式，从地址
	 */
	private String[] slaveAddress;
	/**
	 * 哨兵模式：主名称
	 */
	private String masterName;
	/**
	 * 哨兵模式地址
	 */
	private String[] sentinelAddress;
	/**
	 * 集群模式节点地址
	 */
	private String[] nodeAddress;

	public enum Mode {
		/**
		 * 集群模式，单机：single，主从：master，哨兵模式：sentinel，集群模式：cluster
		 */
		single,
		master,
		sentinel,
		cluster
	}


}
