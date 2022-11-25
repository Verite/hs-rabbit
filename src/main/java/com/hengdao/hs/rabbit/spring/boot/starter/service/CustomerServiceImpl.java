package com.hengdao.hs.rabbit.spring.boot.starter.service;

import com.hengdao.hs.rabbit.spring.boot.starter.exception.MqResult;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.LocalCacheMqService;
import com.hengdao.hs.rabbit.spring.boot.starter.messages.dto.MessageStruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Verite
 * @date
 * @apiNote
 */
public class CustomerServiceImpl implements LocalCacheMqService {
    @Autowired
    private LocalCacheMqService localCacheMqService;

    /**
     * 检测消息体
     *
     * @param messageStruct
     * @return
     */
    @Override
    public boolean checked(MessageStruct messageStruct) {
        localCacheMqService.checked(messageStruct);
        return false;
    }

    /**
     * 获取锁的名称
     *
     * @param messageStruct
     * @return
     */
    @Override
    public String getLockName(MessageStruct messageStruct) {
        localCacheMqService.getLockName(messageStruct);
        return null;
    }

    /**
     * 锁定消息唯一标识
     *
     * @param lockName
     * @return
     */
    @Override
    public boolean locked(String lockName) {
        localCacheMqService.locked(lockName);
        return false;
    }

    /**
     * 发送消息
     *
     * @param messageStruct
     * @return
     */
    @Override
    public MqResult send(MessageStruct messageStruct) throws InterruptedException {
        localCacheMqService.send(messageStruct);
        return null;
    }
}
