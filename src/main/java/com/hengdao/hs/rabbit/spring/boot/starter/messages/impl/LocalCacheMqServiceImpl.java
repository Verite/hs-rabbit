package com.hengdao.hs.rabbit.spring.boot.starter.messages.impl;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqResult;
import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqStatus;
import com.hengdao.hs.rabbit.spring.boot.starter.exception.ServiceException;
import com.hengdao.hs.rabbit.spring.boot.starter.config.RabbitProperties;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.LocalCacheMqService;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.dto.MessageStruct;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.RabbitConstant;
import com.hengdao.hs.rabbit.spring.boot.starter.utils.ThreadLocalUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;

/**
 * 单机本地缓存实现rabbit针对一条消息的锁定和解锁
 * @author Verite
 * @date 2022.11.22
 * @apiNote
 */
@Slf4j
@Setter
@Getter
public class LocalCacheMqServiceImpl implements LocalCacheMqService {
    private static final Logger logger = LoggerFactory.getLogger(LocalCacheMqServiceImpl.class);
    /**
     * rabbit 配置项
     */
    private final  RabbitProperties rabbitProperties;
    private   RabbitTemplate rabbitTemplate;
    public LocalCacheMqServiceImpl(RabbitProperties rabbitProperties){
        this.rabbitProperties = rabbitProperties;
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
        String id = messageStruct.getId();
        if (StringUtils.isEmpty(id)) {
            throw new ServiceException(MqStatus.MESSAGE_ID_EMPTY);
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
        return rabbitProperties.getAppid() + messageStruct.getId();
    }

    /**
     * 锁定消息唯一标识
     *
     * @param lockName
     * @return
     */
    @Override
    public boolean locked(String lockName) {
        if(Boolean.TRUE.equals(ThreadLocalUtil.get(lockName))){
            logger.error("lockName:{};message:id已经存在,请稍后再试};",lockName);
            throw new ServiceException("已经存在", MqStatus.LOCK_INVALID);
        }
        ThreadLocalUtil.put(lockName,lockName);
        logger.info("lockName:{};message：已经加锁",lockName);
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
        } catch (Exception var5) {
            var5.printStackTrace();
            logger.error("lockName:{};message: 发送失败 {};",lockName, var5.getMessage());
            ThreadLocalUtil.remove(lockName);
            throw new ServiceException(var5.getMessage(), MqStatus.SEND_INVALID);
        }
        ThreadLocalUtil.remove(lockName);
        return MqResult.success("发送成功", lockName);
    }


}