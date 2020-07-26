package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import de.hsos.kbse.entities.Arduino;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Persistence;

public class SimulatorDataListener {
    // Jms
    private InputStream input;
    private OutputStream output;
    private TopicSession session;
    private TopicPublisher producer;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    private int[] lastValues;
    
    // ?
    private Random rand;
    private Arduino arduino;

    public SimulatorDataListener(Arduino ard) {
        this.arduino = ard;
        rand = new Random();
        
        // Init 'lastValues' for Simulation
        lastValues = new int[6];
        for(int val : lastValues) {
            val = 0;
        }
        
        // init scheduler
        scheduler = Executors.newScheduledThreadPool( 1 );
        
        // init JMS publisher        
        try {
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("jms.Topic");
            producer = session.createPublisher(topic);
        } catch (JMSException ex) {
            Logger.getLogger(SimulatorDataListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SimulatorDataListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.routine();
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
    
    /* Values[]:
        *int waterMeter;
        *int dungMeter;
        *int sunLevel;
        *int airHumidity;
        *int soilHumidity;
        *int temperature;
    */
    public String generateSensordata(int[] lastValues) {
        String csv = "";
        boolean initLastValues = true;
        for(int val : lastValues) {
            if(val != 0) initLastValues = false;
        }
        
        // WaterMeter
        int waterMeter = lastValues[0];
        if(initLastValues) { waterMeter = rand.nextInt(101); }
        waterMeter += rand.nextInt(10)-5;
        if(waterMeter < 0) waterMeter = 0;
        if(waterMeter > 100) waterMeter = 100;
        
        // DungMeter
        int dungMeter = lastValues[1];
        if(initLastValues) { dungMeter = rand.nextInt(101); }
        dungMeter += rand.nextInt(10)-5;
        if(dungMeter < 0) dungMeter = 0;
        if(dungMeter > 100) dungMeter = 100;
        
        // SunLevel
        int sunLevel = lastValues[2];
        if(initLastValues) { sunLevel = rand.nextInt(101); }
        sunLevel += rand.nextInt(20)-10;
        if(sunLevel < 0) sunLevel = 0;
        if(sunLevel > 100) sunLevel = 100;
        
        // AirHumidity
        int airHum = lastValues[3];
        if(initLastValues) { airHum = rand.nextInt(101); }
        airHum += rand.nextInt(4)-2;
        if(airHum < 0) airHum = 0;
        if(airHum > 100) airHum = 100;
        
        // SoilHumidity
        int soilHum = lastValues[4];
        if(initLastValues) { soilHum = rand.nextInt(101); }
        soilHum += rand.nextInt(2)-1;
        if(soilHum < 0) soilHum = 0;
        if(soilHum > 100) soilHum = 100;
        
        // Temperature
        int temp = lastValues[5];
        if(initLastValues) { temp = rand.nextInt(30); }
        temp += rand.nextInt(2)-1;
        if(temp < 0) temp = 0;
        if(temp > 30) temp = 30;
        
        // Build CSV
        csv = String.format("%d,%d,%d,%d,%d,%d", waterMeter, dungMeter, sunLevel, airHum, soilHum, temp);
        
        return csv;
    }
    
    public void routine() {
        scheduler.scheduleAtFixedRate(() -> {
            // generate data
            String generated_val = generateSensordata(lastValues);
            generated_val += "," + arduino.getArduinoId();
            String[] values_split = generated_val.split(",");
            
            // update 'lastValues'
            int i = 0;
            for(int val : lastValues) {
                val = Integer.parseInt(values_split[i]);
                i++;
            }
            
            // sende Message
            sendMessage(values_split);
            
        }, 1, 5, TimeUnit.SECONDS   
        );
    }
    
    
}
