package com.hengdao.hs.rabbit.spring.boot.starter.messages;

import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ常量池
 * @author Verite
 */
public interface RabbitConstant {
	/**
	 * 主题模式
	 */
	String TOPIC_MODE_QUEUE = "topic.mode";

	/**
	 * 客户的route_key
	 */
	String CUSTOMER_ROUTING_KEY_NAME = "customer.routing.name";

}
