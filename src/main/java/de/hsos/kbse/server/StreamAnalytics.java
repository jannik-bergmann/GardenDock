/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.server;

import de.hsos.kbse.entities.User;
import de.hsos.kbse.jms.ConsumerMessageListener;
import de.hsos.kbse.iotGateway.GatewayModeSimulator;
import de.hsos.kbse.iotGateway.IotGatewayInterface;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import de.hsos.kbse.repos.interfaces.UserRepoInterface;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

//@ApplicationScoped
@Singleton
@Named
@NoArgsConstructor
public class StreamAnalytics implements Serializable {
    // IotGateway
    @Inject
    @GatewayModeSimulator
    private IotGatewayInterface iotgateway;
    
    // Repos
    @Inject
    private ArduinoRepoInterface ardRepo;
    @Inject
    private SensordataRepoInterface sensorRepo;
    @Inject
    private UserRepoInterface usrRepo;
    
    // JMS
    @Resource(mappedName = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    @Resource(lookup = "jms.Topic")
    private Topic topic;
    @Inject
    ConsumerMessageListener cml;
    
    // For The Gateways
    private List<User> users;
    
    @PostConstruct
    private void init() {
        // Setup jms connection as receiver
        try {
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber receiver = ses.createSubscriber(topic);
            receiver.setMessageListener(cml);
            iotgateway.startUp();
        } catch ( JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(StreamAnalytics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    @PreDestroy
    private void cleanup() {
       iotgateway.cleanup();
    }
    
    public void waterPumpOn() {
        iotgateway.waterPumpOn();
    }
    
    public void fertilizerPumpOn() {
        iotgateway.dungPumpOn();
    }
    
    public void waterPumpOff() {
        iotgateway.waterPumpOff();
    }
    
    public void fertilizerPumpOff() {
        iotgateway.dungPumpOff();
    }
}
