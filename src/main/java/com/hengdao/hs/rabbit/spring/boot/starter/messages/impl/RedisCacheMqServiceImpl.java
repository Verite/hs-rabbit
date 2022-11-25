package com.hengdao.hs.rabbit.spring.boot.starter.messages.impl;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqResult;
import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqStatus;
import com.hengdao.hs.rabbit.spring.boot.starter.lock.LockType;
import com.hengdao.hs.rabbit.spring.boot.starter.exception.ServiceException;
import com.hengdao.hs.rabbit.spring.boot.starter.config.RabbitProperties;
import com.hengdao.hs.rabbit.spring.boot.starter.lock.RedisLockClient;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.dto.MessageStruct;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.RabbitConstant;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.RedisCacheMqService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;

/**
 * @author Verite
 * @date 2022.11.22
 * @apiNote
 */
@Slf4j
public class RedisCacheMqServiceImpl implements RedisCacheMqService {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheMqServiceImpl.class);
    private final RabbitProperties rabbitProperties;
    private  RabbitTemplate rabbitTemplate;
    private RedisLockClient redisLockClient;

    public RedisCacheMqServiceImpl(RabbitProperties rabbitProperties, RabbitTemplate rabbitTemplate, RedisLockClient redisLockClient) {
        this.rabbitProperties = rabbitProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.redisLockClient = redisLockClient;
    }
    /**
     * 检测消息体
     *
     * @param messageStruct
     * @return
     */
    @Override
    public boolean checked(MessageStruct messageStruct) {
        String message = messageStruct.getMessage();
        if (StringUtils.isEmpty(message)) {
            throw new ServiceException(MqStatus.MESSAGE_TOPIC_EMPTY);
        }
        return true;
    }

    /**
     * 获取锁的名称
     *
     * @param messageStruct
     * @return
     */
    @Override
    public String getLockName(MessageStruct messageStruct) {
        return this.rabbitProperties.getAppid() + messageStruct.getId() + this.rabbitProperties.getSecret();
    }

    /**
     * 锁定消息唯一标识
     *
     * @param lockName
     * @return
     */
    @Override
    public boolean locked(String lockName) {
        if (redisLockClient == null) {
            throw new ServiceException("redis 链接失效", MqStatus.VERIFY_CONNECT);
        }
        try {
            redisLockClient.tryLock(lockName,
                    this.rabbitProperties.getLockType(),
                    this.rabbitProperties.getWaitOut(),
                    this.rabbitProperties.getLeaseTime(),
                    this.rabbitProperties.getTimeUnit());

            logger.info("lockName:{};锁定：{}", lockName, "成功");
        } catch (Throwable var14) {
            var14.printStackTrace();
            logger.error("lockName:{};message:{};", lockName, var14.getMessage());
            throw new ServiceException(var14.getMessage(), MqStatus.LOCK_INVALID);
        }
        return true;
    }

    /**
     * 发送消息
     *
     * @param messageStruct
     * @return
     */
    @Override
    public MqResult send(MessageStruct messageStruct) {
        checked(messageStruct);
        String lockName = getLockName(messageStruct);
        locked(lockName);
        try {
            rabbitTemplate.convertAndSend(RabbitConstant.TOPIC_MODE_QUEUE, RabbitConstant.CUSTOMER_ROUTING_KEY_NAME, messageStruct.getMessage(), new CorrelationData(lockName));
            logger.info("lockName:{};message:{};", lockName, messageStruct.getMessage() + "发送成功");
        } catch (AmqpException var5) {
            var5.printStackTrace();
            logger.error("lockName:{};message:{};", lockName, var5.getMessage());
            redisLockClient.unLock(lockName, LockType.FAIR);
            throw new ServiceException(var5.getMessage(), MqStatus.SEND_INVALID);
        }
        redisLockClient.unLock(lockName, LockType.FAIR);
        return MqResult.success("成功", lockName);
    }

}