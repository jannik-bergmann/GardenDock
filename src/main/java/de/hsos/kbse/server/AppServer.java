/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.server;

import de.hsos.kbse.controller.ArduinoRepository;
import de.hsos.kbse.controller.SensordataRepository;
import de.hsos.kbse.controller.UserRepository;
import de.hsos.kbse.jms.ConsumerMessageListener;
import de.hsos.kbse.iotGateway.GatewayModeSimulator;
import de.hsos.kbse.iotGateway.IotGatewayInterface;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
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
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

@ApplicationScoped
@Named
@NoArgsConstructor
public class AppServer implements Serializable {
    // IotGateway
    @Inject
    @GatewayModeSimulator
    private IotGatewayInterface iotgateway;
    
    // JMS
    @Resource(mappedName = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    @Resource(lookup = "jms.Topic")
    private Topic topic;
    
    // Repos
    @Inject
    private ArduinoRepository ardRepo;
    @Inject
    private SensordataRepository sensorRepo;
    @Inject
    private UserRepository usrRepo;
    
    //@Inject
    //private ConsumerMessageListener cml;
    
    void init(@Observes @Initialized(ApplicationScoped.class) Object init) {  
      try {
            // Setup jms connection as receiver
            Context context = new InitialContext();
            //topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            Connection connection = topicFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(topic);
            ConsumerMessageListener cml = new ConsumerMessageListener();
            consumer.setMessageListener(cml);
            connection.start();          
        } catch (NamingException | JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        iotgateway.routine();
    }
     
        
    @PreDestroy
    void cleanup() {
       iotgateway.cleanup();
    }
}