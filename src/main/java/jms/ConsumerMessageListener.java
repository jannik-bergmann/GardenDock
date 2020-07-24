package jms;

/**
 *
 * @author bastianluhrspullmann
 */

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener {
    
    private final String consumerName;
    public ConsumerMessageListener(String consumerName) {
        this.consumerName = consumerName;
    }
    
    @Override
    public void onMessage(Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            System.out.println(consumerName + " received " + textMessage.getText());
        } catch (JMSException e) {  
            System.err.println("Error while getting JMS Textmessage");
            e.toString();
        }
    }
    
    
    
    
}
