package com.josiah.activemq.subcriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer1 {
    private static final String USERNAME= ActiveMQConnection.DEFAULT_USER; // 默认的连接用户名
    private static final String PASSWORD=ActiveMQConnection.DEFAULT_PASSWORD; // 默认的连接密码
    private static final String BROKEURL=ActiveMQConnection.DEFAULT_BROKER_URL; // 默认的连接地址
    public static void main(String[] args) {
        ConnectionFactory connectionFactory;//连接工厂
        Connection connection = null;//连接
        Session session = null;//会话
        Destination destination  = null;//消息目的地，就是一个消息队列
        MessageConsumer messageConsumer  = null;//消息生产者

        try {
            //1.连接MQ
            connectionFactory = new ActiveMQConnectionFactory(USERNAME,PASSWORD,BROKEURL);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("短信发送队列-发布订阅");
            messageConsumer = session.createConsumer(destination);
            //3.写MQ的监听器
            messageConsumer.setMessageListener(new MyMessageListener1());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

class MyMessageListener1 implements MessageListener{

    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage)message;
            System.out.println("Customer1从MQ获取的消息:" + textMessage.getText());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
