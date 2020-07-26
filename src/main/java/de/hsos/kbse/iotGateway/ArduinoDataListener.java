/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.iotGateway;

import de.hsos.kbse.entities.Sensordata;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author bastianluhrspullmann
 */
public class ArduinoDataListener implements SerialPortDataListener {
    // Jms
    private TopicSession session;
    private TopicPublisher producer;
    
    // Connection 
    private InputStream input;
    private OutputStream output;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    
    // Others
    private SerialPort sp;
    private String newestData;
    
    public ArduinoDataListener(SerialPort sp) {
        this.sp = sp;
        newestData = "";
        
        // init scheduler
        scheduler = Executors.newScheduledThreadPool( 1 );
        
        // Init JMS publisher        
        try {
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("jms.Topic");
            producer = session.createPublisher(topic);
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(SimulatedIotConnection.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;           
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
        byte[] data = new byte[sp.bytesAvailable()];
        int input = sp.readBytes(data, data.length);
        
        // Save Arduino data (CSV)
        if(data != null || input != 1) return;
        newestData = data.toString();

    }
    
    Sensordata getSensordata() {
        return null;
    }
    
    private void routine() {
        scheduler.scheduleAtFixedRate(() -> {
            // get last data drom serial Port
            String[] values_split = null;
            values_split = newestData.split(",");
            
            if(values_split != null) {
                // sende Message
                sendMessage(values_split);
            }
            
        }, 1, 5, TimeUnit.SECONDS   
        );
    }
    
}
