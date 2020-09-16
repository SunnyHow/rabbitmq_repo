package com.ehualu.stdp.rabbitmq.demo;

import com.ehualu.stdp.rabbitmq.demo.security.LoginUtil;
import com.ehualu.stdp.rabbitmq.demo.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import im.yanchen.krb5.auth.passwd.UserGroupInformationLoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @Author SunHao
 * @Date 2020/8/27 13:31
 */
public class RabbitMQHelloProducer {

    private static Logger logger = LoggerFactory.getLogger(RabbitMQHelloProducer.class);

    //安全认证
    private static void securityPrepare() throws IOException {
        String rootDir = System.getProperty("user.dir") + File.separator + "config" + File.separator;
        String userKeytabFile = rootDir + "lixx.keytab";
        String krb5File = rootDir + "krb5.conf";
        LoginUtil.login("lixx",userKeytabFile,krb5File);
    }

    public void run(Channel channel) throws IOException {
        channel.queueDeclare("hello",false,false,false,null);
        channel.basicPublish("","hello",null,"Hello RabbitMQ".getBytes());
    }

    public static void main(String[] args) throws IOException {

        try {
            logger.info("Security prepare start...");
            securityPrepare();
        }catch (IOException e){
            e.printStackTrace();
            logger.error("Security prepare fail...");
            return;
        }
        logger.info("Security prepare success");

        Connection connection = RabbitMQUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("hello",false,false,false,null);

        channel.basicPublish("","hello",null,"Hello RabbitMQ 安全认证 李子航".getBytes());

        RabbitMQUtils.closeConnectionAndChannel(channel,connection);
    }
}
