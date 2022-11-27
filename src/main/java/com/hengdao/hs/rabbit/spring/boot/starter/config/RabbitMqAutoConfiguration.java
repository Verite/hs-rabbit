package com.hengdao.hs.rabbit.spring.boot.starter.config;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.ServiceException;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.RabbitConstant;
import com.hengdao.hs.rabbit.spring.boot.starter.props.RabbitModuleProperties;
import com.hengdao.hs.rabbit.spring.boot.starter.props.RedisLockModuleProperties;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.MessageBusService;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.impl.LocalCacheMqServiceImpl;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.impl.RedisCacheMqServiceImpl;
import com.hengdao.hs.rabbit.spring.boot.starter.rabbitMq.RabbitModuleInitializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.hengdao.hs.rabbit.spring.boot.starter.exception.MqStatus.SEND_INVALID;


/**
 * RabbitMQ配置，主要是配置队列
 *
 * @author yangkai.shen
 */
@Slf4j
@AllArgsConstructor
@AutoConfigureAfter(RedisLockAutoConfiguration.class)
@EnableConfigurationProperties({RabbitModuleProperties.class, RedisLockModuleProperties.class})
@ConditionalOnClass(MessageBusService.class)
public class RabbitMqAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = RedisLockModuleProperties.LOCK_ENABLED, havingValue = "true")
    public MessageBusService redisRabbitMqService(RabbitModuleProperties rabbitModuleProperties) {
        return new RedisCacheMqServiceImpl(rabbitModuleProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageBusService localRabbitMqService(RabbitModuleProperties rabbitModuleProperties) {
        return new LocalCacheMqServiceImpl(rabbitModuleProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplate rabbitTemplate(RabbitModuleProperties rabbitModuleProperties) {
        CachingConnectionFactory connectionFactory = connectionFactory(rabbitModuleProperties);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            /*
             ack 表示消息是否到达了交换机，交换机是否确认收到消息
             cause 存储的是没有到达交换机的原因
            */
            if (!ack) {
                log.info("消息发送失败:correlationData({}),ack(false),cause({})", correlationData, cause);
                throw new ServiceException(cause, SEND_INVALID);
            } else {
                log.info("消息发送成功:correlationData({}),ack(true),cause({})", correlationData, cause);
            }
        });
        // rabbitTemplate.setReturnsCallback((returned -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage()
        // )));
        return rabbitTemplate;
    }

    private CachingConnectionFactory connectionFactory(RabbitModuleProperties rabbitModuleProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setUsername(rabbitModuleProperties.getUsername());
        connectionFactory.setPassword(rabbitModuleProperties.getPassword());
        connectionFactory.setPort(rabbitModuleProperties.getPort());
        connectionFactory.setHost(rabbitModuleProperties.getHost());
        return connectionFactory;
    }

    //声明队列
    @Bean
    public Queue customerRoutingKeyName(){
        Queue queue = new Queue(RabbitConstant.CUSTOMER_ROUTING_KEY_NAME,true);
        //设置队列属性
        return queue;
    }

    //声明订阅模式交换机
    @Bean
    public TopicExchange topicModeQueue(){
        return new TopicExchange(RabbitConstant.TOPIC_MODE_QUEUE,true,false);
    }

    //绑定队列
    @Bean
    public Binding bindingCustomerRoutingKeyName(Queue customerRoutingKeyName, TopicExchange topicModeQueue){
        return BindingBuilder.bind(customerRoutingKeyName).to(topicModeQueue).with(RabbitConstant.CUSTOMER_ROUTING_KEY_NAME_KEY);
    }

    /**
     * 使用json序列化机制，进行消息转换
     */
    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 动态创建队列、交换机初始化器
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitModuleInitializer rabbitModuleInitializer(AmqpAdmin amqpAdmin, RabbitModuleProperties rabbitModuleProperties) {
        return new RabbitModuleInitializer(amqpAdmin, rabbitModuleProperties);
    }

}
