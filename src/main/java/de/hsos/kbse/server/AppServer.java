/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.server;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.User;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.SensordataRepository;
import de.hsos.kbse.repos.UserRepository;
import de.hsos.kbse.jms.ConsumerMessageListener;
import de.hsos.kbse.iotGateway.GatewayModeSimulator;
import de.hsos.kbse.iotGateway.IotGatewayInterface;
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
import javax.inject.Singleton;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
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
    
    @Inject
    ConsumerMessageListener cml;
    

    void init(@Observes @Initialized(ApplicationScoped.class) Object init) {  
      try {         
            // Setup jms connection as receiver
            InitialContext ctx = new InitialContext();
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber receiver = ses.createSubscriber(topic);
            receiver.setMessageListener(cml);
            iotgateway.startUp();
        } catch (NamingException | JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        iotgateway.routine();
        
        
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            transactionManager.begin();
        } catch (NotSupportedException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Arduino ard = new Arduino();
        User temp = new User();
        //usrRepo.addUser(temp);
        entityManager.persist(temp);
        ard.setUser(temp);
        ard.setComPort("asfas");
        ard.setName("asggsd");
        //ardRepo.addArduino(ard);
        entityManager.persist(ard);
        entityManager.close();      
        try {
            transactionManager.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
        
    @PreDestroy
    void cleanup() {
       iotgateway.cleanup();
    }
}
