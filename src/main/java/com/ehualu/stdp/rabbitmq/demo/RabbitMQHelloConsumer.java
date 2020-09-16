package com.ehualu.stdp.rabbitmq.demo;

import com.ehualu.stdp.rabbitmq.demo.security.LoginUtil;
import com.ehualu.stdp.rabbitmq.demo.utils.RabbitMQUtils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @Author SunHao
 * @Date 2020/8/27 14:28
 */
public class RabbitMQHelloConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQHelloConsumer.class);

    //消息监听
    public static void listen(Channel channel) throws IOException {
        //通道绑定对象
        channel.queueDeclare("hello",false,false,false,null);

        //消费消息
        //参数1：消费哪个队列的消息，队列名称
        //参数2：开始消息的自动确认机制
        //参数3：消费时的回调接口
        channel.basicConsume("hello", true,new DefaultConsumer(channel){
            //最后一个参数：消息队列中取出的消息
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Hello Consumer = "+new String(body));
            }
        });
    }

    //安全认证
    private static void securityPrepare() throws IOException {
        String rootDir = System.getProperty("user.dir") + File.separator + "config" + File.separator;
        String userKeytabFile = rootDir + "user.keytab";
        String krb5File = rootDir + "krb5.conf";
        System.setProperty("sun.security.krb5.debug", "true");
        LoginUtil.setKrb5Config(krb5File);
        LoginUtil.setJaasFile("user@HADOOP.COM",userKeytabFile);
    }

    public static void main(String[] args) throws IOException {
        Connection connection = RabbitMQUtils.getConnection();
        Channel channel = connection.createChannel();

        try {
            logger.info("Security prepare start...");
            securityPrepare();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Security prepare fail...");
            return;
        }
        logger.info("Security prepare success...");


        listen(channel);
    }
}
