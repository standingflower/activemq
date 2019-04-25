package com.josiah.activemq.subcriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Producer {

    private static final String USERNAME= ActiveMQConnection.DEFAULT_USER; // 默认的连接用户名
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // 默认的连接密码
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // 默认的连接地址

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;//连接工厂
        Connection connection = null;//连接
        Session session = null;//会话
        Destination destination  = null;//消息目的地，就是一个消息队列
        MessageProducer messageProducer  = null;//消息生产者
        try {
            //1.连接MQ
            System.out.println("username:"+USERNAME);//默认为null
            System.out.println("password:"+PASSWORD);//默认为null
            System.out.println("url:"+BROKEURL);
            connectionFactory = new ActiveMQConnectionFactory(USERNAME,PASSWORD,BROKEURL);
            connection = connectionFactory.createConnection();
            connection.start();
            /**
             * 和p2p一样
             */
            session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);
            /**
             * 发布订阅只需要修改这里即可
             */
            destination = session.createTopic("短信发送队列-发布订阅");
            messageProducer = session.createProducer(destination);

            //2.发送消息
            for (int i=0;i<10;i++){
                String txt = "消息"+i;
                TextMessage txtMsg = session.createTextMessage(txt);
                messageProducer.send(destination,txtMsg);
            }
            session.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("关闭");
            //3.断开
            try{
                messageProducer.close();
                session.close();
                connection.close();
            }catch (Exception e){

            }
        }
    }
}
