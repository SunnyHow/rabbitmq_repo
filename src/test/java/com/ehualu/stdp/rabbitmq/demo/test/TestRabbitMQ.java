package com.ehualu.stdp.rabbitmq.demo.test;

import com.ehualu.stdp.rabbitmq.demo.RabbitmqSecurityDemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author SunHao
 * @Date 2020/8/27 16:48
 *
 * 交换机和队列的生成不取决于生产者，而是取决于消费者
 */
@SpringBootTest(classes = RabbitmqSecurityDemoApplication.class)
@RunWith(SpringRunner.class)
public class TestRabbitMQ {

    //注入rabbitmqTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //Hello World
    @Test
    public void testHello(){
        rabbitTemplate.convertAndSend("hello","hello world i am sunhao");
    }

    //work
    @Test
    public void testWork(){
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work","work模型"+i);
        }
    }

    //fanout 广播
    @Test
    public void testFanout(){
        rabbitTemplate.convertAndSend("logs","","Fanout的模型发送的消息");
    }

    //routing 路由模式
    @Test
    public void testRoute(){
        rabbitTemplate.convertAndSend("directs","error","发送info的key的路由信息");
    }

    //topic 动态路由    订阅模式
    @Test
    public void testTopic(){
        rabbitTemplate.convertAndSend("topics","product.save.add","product.save.add 的路由消息");
    }
}
