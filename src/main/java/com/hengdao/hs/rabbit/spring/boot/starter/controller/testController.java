package com.hengdao.hs.rabbit.spring.boot.starter.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Verite
 * @date
 * @apiNote
 */
public class testController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void test() {
        rabbitTemplate.convertAndSend("log.exchange","log.operation.queue.key","tesst");
    }

    @RabbitListener(queues = "log.operation.queue")
    public void handleInventoryOperation(String inventoryDTO) {
        System.out.println(inventoryDTO);
    }


}
