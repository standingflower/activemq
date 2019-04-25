package com.josiah.activemq.p2p;

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
            /**
             * createSession有两个参数：事务支持事务，确认模式
             * 消息在处理后，需要告知MQ此消息是否可以从队列中移出
             * 确认模式有：(主要针对消费者)
             *  AUTO_ACKNOWLEDGE：自动确认，当消费接收到消息时，会自动的向ActiveMq发送收到消息确认
             *  CLIENT_ACKNOWLEDGE：消费者手动调用Message的acknowledge方法手动确认收到消息
             *  DUPS_OK_ACKNOWLEDGE：不必确认已收到消息。但它可能会引起消息的重复接收，即一个消息被处理多次，而且消息一直存在队列中
             *  SESSION_TRANSACTED：事务
             */
            /**
             * 这里创建session的方式可以和生产者创建反射不同
             */
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            destination = session.createQueue("短信发送队列-点对点");
            /**
             * 是否读取消息的条件，当messageSelector为真时，才会读取消息
             */
            String messageSelector = "i>8";
            messageConsumer = session.createConsumer(destination,messageSelector);

            //2.从MQ服务器取消息
            for (int i=0;i<10;i++){
                System.out.println(i);
                /**
                 * receive：主动获取消息，如果消息队列中无消息，线程阻塞，等待获取
                 * ActiveMQ获取消息的顺序看谁先创建messageConsumer对象
                 */
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                System.out.println(textMessage.getText());
            }
            /**
             * 获取后，需执行commit，MQ才知道消费获取到成功，然后在MQ中删除对应的Message
             */
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
