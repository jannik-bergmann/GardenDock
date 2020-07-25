package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import de.hsos.kbse.jms.ConsumerMessageListener;
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
import javax.jms.MapMessage;
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
    @Inject
    MessageProducer producer;
    @Inject
    Session session;
    
    // Simulator
    @Inject
    private IotSimulator simulator;
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
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void simulateData() {
        if(closed) { return; }
        TextMessage message;
        String sim_value = simulator.generateSensordata(lastValues);
        sim_value += ",176ba88b-3b64-4e65-9705-6111b7b9da08";
        
        String[] values_split = sim_value.split(",");
        sendMessage(values_split);
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