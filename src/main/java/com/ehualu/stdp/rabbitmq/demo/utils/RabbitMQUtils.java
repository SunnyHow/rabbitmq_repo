package com.ehualu.stdp.rabbitmq.demo.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author SunHao
 * @Date 2020/8/27 13:34
 */
public class RabbitMQUtils {

    private static ConnectionFactory connectionFactory;

    static{
        //重量级资源，类加载时执行，只执行一次
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.38.40.161");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/ems");
        connectionFactory.setUsername("ems");
        connectionFactory.setPassword("123");
    }

    //提供定义连接对象的方法
    public static Connection getConnection(){
        try{
            return connectionFactory.newConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //关闭通道和关闭连接的工具方法
    public static void closeConnectionAndChannel(Channel channel,Connection connection){
        try{
            if (channel != null) {channel.close();}
            if (connection != null){connection.close();}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
