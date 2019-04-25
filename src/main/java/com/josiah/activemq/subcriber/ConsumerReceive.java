package com.josiah.activemq.subcriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 同步点对点获取
 */
public class ConsumerReceive {
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
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            destination = session.createTopic("短信发送队列-发布订阅");
            messageConsumer = session.createConsumer(destination);
            //2.从MQ服务器取消息
            for (int i=0;i<2;i++){
                System.out.println(i);
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                System.out.println(textMessage.getText());
            }
            session.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //3.断开
            try{
                messageConsumer.close();
                session.close();
                connection.close();
            }catch (Exception e){

            }
        }
    }
}
