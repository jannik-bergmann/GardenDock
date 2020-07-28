/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.jms;

import javax.annotation.Resource;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;

/**
 *
 * @author Bastian Lührs-Püllmann
 */
public class JmsBuilder {
    @Resource(lookup = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    
    @Resource(lookup = "jms.Topic")
    private Topic topic;
    
    @Produces 
    private Connection createTopicConnection() throws JMSException {
        return topicFactory.createConnection();
    }
    
    public void closeTopicConnection(@Disposes Connection connection) throws JMSException {
        connection.close();
    }
    
    @Produces 
    public Session createOrderSession(Connection connection) throws JMSException {
        return connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    }
    
    public void closeOrderSession(@Disposes Session session) throws JMSException {
        session.close();
    }
    
    @Produces
    public MessageProducer createOrderMessageProducer(Session session) throws JMSException {
        return session.createProducer(topic);
    }

    public void closeOrderMessageProducer(@Disposes MessageProducer producer) throws JMSException {
        producer.close();
    }
    

}
