package com.hengdao.hs.rabbit.spring.boot.starter.config;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.ServiceException;
import com.hengdao.hs.rabbit.spring.boot.starter.lock.RedisLockClient;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.MessageBusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.hengdao.hs.rabbit.spring.boot.starter.exception.MqStatus.SEND_INVALID;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.impl.*;


/**
 * RabbitMQ配置，主要是配置队列
 *
 * @author yangkai.shen
 */
@Slf4j
@AllArgsConstructor
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties({RabbitProperties.class})
@ConditionalOnClass(MessageBusService.class)
public class RabbitMqAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = RabbitProperties.REDIS_CACHE_ENABLED, matchIfMissing = true)
    public MessageBusService redisRabbitMqService(RabbitProperties rabbitProperties) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        RedisLockClient redisLockClient = null;
        return new RedisCacheMqServiceImpl(rabbitProperties, rabbitTemplate, redisLockClient);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(RabbitProperties rabbitProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setHost(rabbitProperties.getHost());
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
        rabbitTemplate.setReturnsCallback((returned -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage()
        )));
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageBusService localRabbitMqService(RabbitProperties rabbitProperties) {
        return new LocalCacheMqServiceImpl(rabbitProperties);
    }
}
