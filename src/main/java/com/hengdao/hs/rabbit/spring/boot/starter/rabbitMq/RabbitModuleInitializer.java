package com.hengdao.hs.rabbit.spring.boot.starter.rabbitMq;

import com.hengdao.hs.rabbit.spring.boot.starter.props.RabbitModuleProperties;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ队列初始化器
 * @author 93757
 */
@Slf4j
public class RabbitModuleInitializer implements SmartInitializingSingleton {

    private AmqpAdmin amqpAdmin;
    //设置交换机 队列
    // modules:
    //         # 延时队列，到了过期的时间会被转发到死信队列
    //   -     routing-key: log.operation.queue.key
    //         queue:
    //         name: log.operation.queue
    //               #          dead-letter-exchange: order.exchange
    //               #          dead-letter-routing-key: order.close.routing.key
    //         arguments:
    //                 # 1分钟(测试)，单位毫秒
    //         x-message-ttl: 60000
    //         exchange:
    //         name: log.exchange
    private RabbitModuleProperties rabbitModuleProperties;

    public RabbitModuleInitializer(AmqpAdmin amqpAdmin, RabbitModuleProperties rabbitModuleProperties) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitModuleProperties = rabbitModuleProperties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        log.info("RabbitMQ 根据配置动态创建和绑定队列、交换机");
        declareRabbitModule();
    }

    /**
     * RabbitMQ 根据配置动态创建和绑定队列、交换机
     */
    private void declareRabbitModule() {
        List<RabbitModuleInfo> rabbitModuleInfos = rabbitModuleProperties.getModules();
        if (CollectionUtils.isEmpty(rabbitModuleInfos)) {
            return;
        }
        for (RabbitModuleInfo rabbitModuleInfo : rabbitModuleInfos) {
            configParamValidate(rabbitModuleInfo);

            // 队列
            Queue queue = convertQueue(rabbitModuleInfo.getQueue());
            // 交换机
            Exchange exchange = convertExchange(rabbitModuleInfo.getExchange());
            // 绑定关系
            String routingKey = rabbitModuleInfo.getRoutingKey();
            String queueName = rabbitModuleInfo.getQueue().getName();
            String exchangeName = rabbitModuleInfo.getExchange().getName();
            Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);

            // 创建队列
            amqpAdmin.declareQueue(queue);
            // 创建交换机
            amqpAdmin.declareExchange(exchange);
            // 队列 绑定 交换机
            amqpAdmin.declareBinding(binding);
        }
    }

    /**
     * RabbitMQ动态配置参数校验
     *
     * @param rabbitModuleInfo
     */
    public void configParamValidate(RabbitModuleInfo rabbitModuleInfo) {

        String routingKey = rabbitModuleInfo.getRoutingKey();

        Assert.isTrue(StringUtil.isNotBlank(routingKey), "RoutingKey 未配置");

        Assert.isTrue(rabbitModuleInfo.getExchange() != null,String.format("routingKey:{}未配置exchange", routingKey));
        Assert.isTrue(StringUtil.isNotBlank(rabbitModuleInfo.getExchange().getName()),String.format("routingKey:{}未配置exchange的name属性", routingKey));

        Assert.isTrue(rabbitModuleInfo.getQueue() != null, String.format("routingKey:{}未配置queue", routingKey));
        Assert.isTrue(StringUtil.isNotBlank(rabbitModuleInfo.getQueue().getName()), String.format("routingKey:{}未配置exchange的name属性", routingKey));

    }

    /**
     * 转换生成RabbitMQ队列
     *
     * @param queue
     * @return
     */
    public Queue convertQueue(RabbitModuleInfo.Queue queue) {
        Map<String, Object> arguments = queue.getArguments();

        // 转换ttl的类型为long
        if (arguments != null && arguments.containsKey("x-message-ttl")) {
            arguments.put("x-message-ttl",  10L);
        }

        // 是否需要绑定死信队列
        String deadLetterExchange = queue.getDeadLetterExchange();
        String deadLetterRoutingKey = queue.getDeadLetterRoutingKey();
        if (StringUtil.isNotBlank(deadLetterExchange) && StringUtil.isNotBlank(deadLetterRoutingKey)) {

            if (arguments == null) {
                arguments = new HashMap<>(4);
            }
            arguments.put("x-dead-letter-exchange", deadLetterExchange);
            arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);

        }

        return new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), arguments);
    }


    /**
     * 转换生成RabbitMQ交换机
     *
     * @param exchangeInfo
     * @return
     */
    public Exchange convertExchange(RabbitModuleInfo.Exchange exchangeInfo) {

        AbstractExchange exchange = null;

        RabbitExchangeTypeEnum exchangeType = exchangeInfo.getType();

        String exchangeName = exchangeInfo.getName();
        boolean isDurable = exchangeInfo.isDurable();
        boolean isAutoDelete = exchangeInfo.isAutoDelete();

        Map<String, Object> arguments = exchangeInfo.getArguments();

        switch (exchangeType) {
            case DIRECT:// 直连交换机
                exchange = new DirectExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            case TOPIC: // 主题交换机
                exchange = new TopicExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            case FANOUT: // 扇形交换机
                exchange = new FanoutExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            case HEADERS: // 头交换机
                exchange = new HeadersExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            default:
                break;
        }
        return exchange;
    }
}
