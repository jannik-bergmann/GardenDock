/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse;

import dbController.ArduinoRepository;
import dbController.SensordataRepository;
import dbController.UserRepository;
import entities.Arduino;
import entities.Sensordata;
import entities.User;
import jms.ConsumerMessageListener;
import iotGateway.GatewayModeSimulator;
import iotGateway.IotGatewayInterface;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 *
 * @author Basti's
 */

@ApplicationScoped
public class AppServer implements Serializable {
    // IotGateway
    @Inject
    @GatewayModeSimulator
    private IotGatewayInterface iotgateway;
    
    // JMS
    @Resource(mappedName = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    
    // Repos
    @Inject
    private ArduinoRepository ardRepo;
    @Inject
    private SensordataRepository sensorRepo;
    @Inject
    private UserRepository usrRepo;
    
    private EntityManager em;
    
    public AppServer() {
        try {
            // Setup jms connection as receiver
            Context context = new InitialContext();
            topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            Connection connection = topicFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("jms.Topic");
            
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new ConsumerMessageListener("consumer 1"));
            connection.start();          
        } catch (NamingException | JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void init(@Observes @Initialized(ApplicationScoped.class) Object init) {  
        User usr = new User();
        usrRepo.addUser(usr);
        iotgateway.routine();
    }
     
    @PreDestroy
    void cleanup() {
       iotgateway.cleanup();
    }
}
