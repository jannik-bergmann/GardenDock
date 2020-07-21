/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.ping.boundary;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author bastianluhrspullmann
 */
public class ConsumerMessageListener implements MessageListener {
    
    private String consumerName;
    public ConsumerMessageListener(String consumerName) {
        this.consumerName = consumerName;
    }
    
    @Override
    public void onMessage(Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            System.out.println(consumerName + " received " + textMessage.getText());
        } catch (JMSException e) {          
            e.printStackTrace();
        }
    }
    
    
}
