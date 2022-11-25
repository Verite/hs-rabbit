package com.hengdao.hs.rabbit.spring.boot.starter.messages;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqResult;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.dto.MessageStruct;

/**
 * @author Verite
 * @date 2022.11.23
 * @apiNote
 */
public interface MessageBusService {
    /**
     * 检测消息体
     * @param messageStruct
     * @return
     */
    boolean checked(MessageStruct messageStruct);

    /**
     * 获取锁的名称
     * @param messageStruct
     * @return
     */
    String getLockName(MessageStruct messageStruct);

    /**
     * 锁定消息唯一标识
     * @param lockName
     * @return
     */
    boolean locked(String lockName);

    /**
     * 发送消息
     * @param messageStruct
     * @return
     */
    MqResult send(MessageStruct messageStruct) throws InterruptedException;
}
