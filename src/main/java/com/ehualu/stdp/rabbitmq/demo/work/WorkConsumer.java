package com.ehualu.stdp.rabbitmq.demo.work;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author SunHao
 * @Date 2020/8/27 17:06
 */
@Component
public class WorkConsumer {

    //消费者1
    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void receive1(String message){
        System.out.println("message1 = "+message);
    }

    //消费者2
    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void receive2(String message){
        System.out.println("message2 = "+message);
    }
}
