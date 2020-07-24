package iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import jms.ConsumerMessageListener;
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
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ManagedBean
@GatewayModeSimulator
public class IotGatewaySimulator implements IotGatewayInterface{
    // Jms
    private InputStream input;
    private OutputStream output;
    // Simulator
    @Inject
    private IotSimulator simulator;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private int[] lastValues;
    // Scheduler
    private ScheduledExecutorService scheduler;
    private boolean closed;
    
    // Constructor
    public IotGatewaySimulator () {
        scheduler = Executors.newScheduledThreadPool( 1 );
        System.out.println("IotGateway erstellt");
        closed = false;
    }
    
    @PostConstruct
    @Override
    public void init() {
        // Setup jms connection as sender
        try {
            // Build up connection
            InitialContext context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            connection = topicFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false -> not transacted, AUTO_ACKNOWLEDGE -> automatisch Nachrichten entgegennehmen
            Topic topic = (Topic) context.lookup("jms.Topic");
            connection.start();
            producer = session.createProducer(topic);
            
            // Init 'lastValues' for Simulation
            lastValues = new int[6];
            for(int val : lastValues) {
                val = 0;
            }
            
        } catch (NamingException | JMSException  ex) {
            System.err.println(ex.toString());
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        this.closed = true;
        
        // Close jms connection
        try {
            this.scheduler.shutdownNow();
            this.scheduler.shutdown();
            this.connection.close();
            this.producer.close();
            this.session.close();
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.toString());
        }
    }
    
    public void simulateData() {
        if(closed) { return; }
        TextMessage message;
        try {
            String sim_value = simulator.generateSensordata(lastValues);         
            
            // TODO: data validation // where to validate? App Server or Gateway
            
            message = session.createTextMessage();
            message.setText("Simulated Data: " + sim_value);
            producer.send(message);
        } catch (JMSException ex) {
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
    public void waterPumoOn() {
        System.out.println("Waterpump on");
    }

    @Override
    public void dungPumpOn() {
        System.out.println("Fertelizerpump on");
    }

    @Override
    public void waterPumoOff() {
        System.out.println("Waterpump off");
    }

    @Override
    public void dungPumpOff() {
        System.out.println("Fertelizerpump off");
    }
   
}
