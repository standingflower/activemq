package com.josiah.activemq;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener{

    public void onMessage(Message message) {
        try {
           TextMessage textMessage = (TextMessage)message;
           System.out.println("从MQ获取的消息:" + textMessage.getText());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
