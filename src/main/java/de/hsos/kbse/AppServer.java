/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse;

import jms.ConsumerMessageListener;
import iotGateway.GatewayModeSimulator;
import iotGateway.IotGatewayInterface;
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

/**
 *
 * @author Basti's
 */

@ApplicationScoped
@Named
public class AppServer implements Serializable {
    // IotGateway
    @Inject
    @GatewayModeSimulator
    private IotGatewayInterface iotgateway;
    
    // JMS
    @Resource(mappedName = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    
    // Repos
    /*
    @Inject
    private ArduinoRepository ardRepo;
    @Inject
    private SensordataRepository sensorRepo;
    @Inject
    private UserRepository usrRepo;
    */
    
    // 
   
    
    
    @Inject
    private ConsumerMessageListener cml;
    
    public AppServer() {

    }
    
    public void doDebug(){
        System.out.println("de.hsos.kbse.AppServer.doDebug()");
        try {
            // Setup jms connection as receiver
            Context context = new InitialContext();
            topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            Connection connection = topicFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("jms.Topic");
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(cml);
            connection.start();          
        } catch (NamingException | JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        iotgateway.routine();
        //cml.persistSensorData("Hallo :)");
        
    }
    
    /*
    void init(@Observes @Initialized(ApplicationScoped.class) Object init) {  
        
        
        
        iotgateway.routine();
       
        /*
        Arduino ard = new Arduino();
        User temp = new User();
        usrRepo.addUser(temp);
        ard.setUser(temp);
        ard.setComPort("asfas");
        ard.setName("asggsd");
        ardRepo.addArduino(ard);
        
    }
     */
        
    @PreDestroy
    void cleanup() {
       iotgateway.cleanup();
    }
}
