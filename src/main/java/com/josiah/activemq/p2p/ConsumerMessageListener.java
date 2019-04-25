package com.josiah.activemq.p2p;

import com.josiah.activemq.MyMessageListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 异步点对点获取
 */
public class ConsumerMessageListener {
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
            /**
             * 使用监听器的方式，好像不能使用事务的方式获取消息，最终不会执行commit
             */
            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("短信发送队列-点对点");
            messageConsumer = session.createConsumer(destination);
            //2.写MQ的监听器
            /**
             * ConsumerReceive方式每次都需要连接，而监听者可以一直监听
             */
            messageConsumer.setMessageListener(new MyMessageListener());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //3.断开
        }
    }
}
