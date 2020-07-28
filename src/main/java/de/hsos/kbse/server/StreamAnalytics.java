package de.hsos.kbse.server;

import de.hsos.kbse.entities.User;
import de.hsos.kbse.iotGateway.GatewayModeSimulator;
import de.hsos.kbse.jms.ConsumerMessageListener;
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
import lombok.NoArgsConstructor;

/** Entry for Analyzing data by creating JMS Listener
 *
 * @author Bastian Luehrs-Puellmann
 */

@Singleton
@Named
@NoArgsConstructor
public class StreamAnalytics implements Serializable {
    // IotGateway
    @Inject
    @GatewayModeSimulator
    private IotGatewayInterface iotgateway;
    
    // JMS
    @Resource(mappedName = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    
    @Resource(lookup = "jms.Topic")
    private Topic topic;
    
    @Inject
    private ConsumerMessageListener cml;
   
    private TopicConnection con;
    
    // For The Gateways
    private List<User> users;

    
    /** Init the JMS Listener and IotGateway
    */
    @PostConstruct
    private void init() {
        // Setup jms connection as receiver
        try {
            con = topicFactory.createTopicConnection();
            con.start();
            TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber receiver = ses.createSubscriber(topic);
            ses.createSubscriber(topic).setMessageListener(cml);
        } catch ( JMSException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(StreamAnalytics.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        iotgateway.startUp();
    }
     
    /** Closes Connection and calls iotgateway cleanup method
    */
    @PreDestroy
    private void cleanup() {
        try {
            con.close();
        } catch (JMSException ex) {
            Logger.getLogger(StreamAnalytics.class.getName()).log(Level.SEVERE, null, ex);
        }
        iotgateway.cleanup();
    }
    
    public void waterPumpOn(String arduinoID) {
        iotgateway.waterPumpOn(arduinoID);
    }
    
    public void fertilizerPumpOn(String arduinoID) {
        iotgateway.fertilizerPumpOn(arduinoID);
    }
    
    public void waterPumpOff(String arduinoID) {
        iotgateway.waterPumpOff(arduinoID);
    }
    
    public void fertilizerPumpOff(String arduinoID) {
        iotgateway.fertilizerPumpOff(arduinoID);
    }
}
