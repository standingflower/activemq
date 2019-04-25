package com.josiah.activemq.p2p;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Producer {

    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER; // 默认的连接用户名
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD; // 默认的连接密码
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL; // 默认的连接地址

    ConnectionFactory connectionFactory;//连接工厂
    Connection connection = null;//连接
    Session session = null;//会话
    Destination destination = null;//消息目的地，就是一个消息队列
    MessageProducer messageProducer = null;//消息生产者


    public static void main(String[] args) {
        Producer producer = new Producer();
        //获取连接
        Connection connection = producer.getConnection();
        producer.transactionQueue(connection);
    }

    private void transactionQueue(Connection connection) {
        try {
            /**
             * 连接默认是关闭的，需要手动开启
             */
            connection.start();
            /**
             * createSession有两个参数：事务支持事务，确认模式
             * 确认模式有：(主要针对消费者)
             *  AUTO_ACKNOWLEDGE
             *  CLIENT_ACKNOWLEDGE
             *  DUPS_OK_ACKNOWLEDGE
             *  SESSION_TRANSACTED
             * 当支持事务时，确认模式默认为SESSION_TRANSACTED，生产者使用commit提交事务后，MQ才会接收消息
             * 也可以使用rollback回滚
             *
             * 一般生产者都使用事务的方式
             */
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            /**
             * 点对点获取使用Queue作为Destination
             * 点对点中的消息，只能被一个消费者使用
             */
            destination = session.createQueue("短信发送队列-点对点");

            messageProducer = session.createProducer(destination);
            /**
             * 消息传输模式(是否可以序列化)
             *  DeliveryMode：NON_PERSISTENT/PERSISTENT(默认)
             */
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

            //2.发送消息
            for (int i = 0; i < 10; i++) {
                String txt = "消息" + i;
                //Message的类型有很多
                TextMessage txtMsg = session.createTextMessage(txt);
                //消息是否被读取的条件 setXxxProperty
                txtMsg.setIntProperty("i", i);

                /**
                 * send方法还可以传入：
                 *  消息的优先级：0-9。0-4是普通消息，5-9是加急消息，消息默认级别是4。(优先级是理论上的，看CPU)
                 *  存活时间，
                 *  消息传输模式：DeliveryMode.PERSISTENT/DeliveryMode.NON_PERSISTENT
                 */
                messageProducer.send(destination, txtMsg);
//                if (i == 3) {
//                    //回滚事务，MQ将不会保存以提交的事务
//                    session.rollback();
//                    break;
//                }
            }
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //3.断开
            try {
                messageProducer.close();
                session.close();
                connection.close();
            } catch (Exception e) {

            }
        }
    }

    private Connection getConnection() {
        //1.连接MQ
        System.out.println("username:" + USERNAME);//默认为null
        System.out.println("password:" + PASSWORD);//默认为null
        System.out.println("url:" + BROKEURL);// failover://tcp://localhost:61616
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);
        try {
            connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
