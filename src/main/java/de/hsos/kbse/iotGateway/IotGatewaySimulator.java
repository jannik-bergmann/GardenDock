package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.NoArgsConstructor;

@GatewayModeSimulator
@NoArgsConstructor
public class IotGatewaySimulator implements IotGatewayInterface{
    // Jms
    private InputStream input;
    private OutputStream output;
    @Resource(lookup = "jms/TopicFactory")
    private TopicConnectionFactory topicFactory;
    @Resource(lookup = "jms.Topic")
    private Topic topic;
    private TopicSession session;
    private TopicPublisher producer;
    
    // Simulator
    @Inject
    private IotSimulator simulator;
    private int[] lastValues;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    private boolean closed;
    
    // Constructor
    
    @PostConstruct
    @Override
    public void init() {
        // Init 'lastValues' for Simulation
        lastValues = new int[6];
        for(int val : lastValues) {
            val = 0;
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        this.closed = true;
        
        // Close jms connection
        this.scheduler.shutdownNow();
        this.scheduler.shutdown();
    }
    
    private void sendMessage(String[] values_split) {
        try {
            MapMessage msg = session.createMapMessage();
            msg.setInt("waterMeter", Integer.parseInt(values_split[0]));
            msg.setInt("dungMeter", Integer.parseInt(values_split[1]));
            msg.setInt("sunLevel", Integer.parseInt(values_split[2]));
            msg.setInt("airHumidity", Integer.parseInt(values_split[3]));
            msg.setInt("soilHumidity", Integer.parseInt(values_split[4]));
            msg.setDouble("temperature", Double.parseDouble(values_split[5]));
            msg.setString("arduinoId", values_split[6]);
            producer.send(msg);
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void simulateData() {
        if(closed) { return; }
        TextMessage message;
        String sim_value = simulator.generateSensordata(lastValues);
        sim_value += ",a67ca578-c5aa-4c85-868b-198975128bcc";
        
        String[] values_split = sim_value.split(",");
        sendMessage(values_split);
    }
    
    @Override
    public void startUp() {
        try {
            scheduler = Executors.newScheduledThreadPool( 1 );
            InitialContext ctx=new InitialContext();
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            //2) create queue session
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //3)create TopicPublisher object
            producer = session.createPublisher(topic);
            System.out.println("IotGateway erstellt");
            closed = false;
        } catch (NamingException | JMSException ex) {
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void routine() {
        // jms & arduino
        /* 
            - alle 5 Sekunden Daten auslesen vom arduino oder aus Simulatorklasse
            - diese dann per jms an das topic "sensordata" schicken
        
            - auf Nachrichten vom topic "actuators" warten --> s.o. geschieht über consumer vom Typ ConsumerMEssageListener
            - diese ggf. ausführen
        */
        scheduler.scheduleAtFixedRate(() -> {
            simulateData();
        }, 1, 5, TimeUnit.SECONDS   
        );
    }

    @Override
    public void waterPumpOn() {
        System.out.println("Waterpump on");
    }

    @Override
    public void dungPumpOn() {
        System.out.println("Fertelizerpump on");
    }

    @Override
    public void waterPumpOff() {
        System.out.println("Waterpump off");
    }

    @Override
    public void dungPumpOff() {
        System.out.println("Fertelizerpump off");
    }
   
}
