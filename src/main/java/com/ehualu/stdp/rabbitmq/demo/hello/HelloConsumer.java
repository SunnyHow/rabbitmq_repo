package com.ehualu.stdp.rabbitmq.demo.hello;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author SunHao
 * @Date 2020/8/27 13:25
 */
@Component  //持久化   非独占     不是自动删除队列
@RabbitListener(queuesToDeclare = @Queue(value = "hello",durable = "true",autoDelete = "true"))
public class HelloConsumer {

    @RabbitHandler
    public void receive(String message){
        System.out.println("message =" + message);
    }
}
