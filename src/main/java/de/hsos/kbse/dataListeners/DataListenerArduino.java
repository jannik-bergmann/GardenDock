/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.dataListeners;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.iotGateway.IotGatewaySimulator;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
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

@Named
@RequestScoped
public class DataListenerArduino implements SerialPortDataListener, DataListener {
    // Jms
    private TopicSession session;
    private TopicPublisher producer;
    private TopicPublisher emptyWarner;
    
    // Connection 
    private OutputStream output;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    
    // Others
    private SerialPort sp;
    
    // Arduino
    private Arduino arduino;
    private String newestDataAsString;
    
    // Repo
    private ArduinoRepoInterface arduinoRepo;
    
    // Constructor
    public DataListenerArduino() {
        newestDataAsString = "";
        
        // Repos
        this.arduinoRepo = new ArduinoRepository();
        
        // init scheduler
        scheduler = Executors.newScheduledThreadPool( 1 );
        
        // Init JMS publisher        
        try {
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topicProducer = (Topic) context.lookup("jms.Topic");
            producer = session.createPublisher(topicProducer);
            
            Topic topicWarnings = (Topic) context.lookup("jms.Warning");
            emptyWarner = session.createPublisher(topicWarnings);
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(DataListenerSimulated.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.routine();
    }
    
    @Override
    public void setArduino(Arduino ard) {
        this.arduino = ard;
    }
    
    public void setSerialPort(SerialPort sp) {
        this.sp = sp;
    }
    
    public SerialPort getSerialPort() {
        return this.sp;
    }
    
    // Send jms message to topic emptyTankWarning
    private void sendTankEmptyWarning(String tank) {
        if(tank.equals("water")) {
            try {
                TextMessage msg = session.createTextMessage();
                msg.setText("Watertank empty");
                emptyWarner.publish(msg);
            } catch (JMSException ex) {
                Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(tank.equals("fertilizer")) {
            try {
                TextMessage msg = session.createTextMessage();
                msg.setText("Fertilizertank empty");
                emptyWarner.publish(msg);
            } catch (JMSException ex) {
                Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendMessage(String[] values_split) {
        // Check for empty tanks
        if(values_split[0].equals("0")) {
            sendTankEmptyWarning("water");
        }
        if(values_split[1].equals("0")) {
            sendTankEmptyWarning("fertilizer");
        }
        
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
    
    private void routine() {
        scheduler.scheduleAtFixedRate(() -> {
            // get last data drom serial Port
            String[] values_split = null;
            values_split = newestDataAsString.split(",");
           
            currentToSetValue();
            
            // sende Message
            if(values_split != null) {
                sendMessage(values_split);
            }    
        }, 1, 5, TimeUnit.SECONDS   
        );
    }
    
    // Routine for automatically water and fertilize the bed of the arduino
    @Override
    public void currentToSetValue() {
        // TODO: eleganter loesen --> evtl. ueber databse
        int currentSoilhumidity = Integer.parseInt(newestDataAsString.split(",")[4]);
        // Check soilhumidity
        if(arduino.getSetWaterLevel() > currentSoilhumidity) {
            // Water
            Runnable waterThread = () -> {
                System.out.println("Water Thread running");
                this.waterOn();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.waterOff();
            };         
        }
        
        // Check timer for fertilizing
        if(true) {
            // Fertilizer
            Runnable fertilizerThread = () -> {
                System.out.println("Fert Thread running");
                this.fertilizerOn();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.fertilizerOff();
            };
        }
    }
    
    @Override
    public void close() {
        scheduler.shutdownNow();
    }
    
    @Override
    public String getArdId() {
        return this.arduino.getArduinoId();
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
        newestDataAsString = data.toString();
    }
    
    @Override
    public void waterOn() {
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("water, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }
    
    @Override
    public void fertilizerOn() {
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("dung, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
        
        // Update Ferilization date in DB
        arduino.setLastFertilization(LocalDateTime.now());
        arduinoRepo.updateArduino(arduino);
    }   
    
    @Override
    public void waterOff() {
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("water, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }
    
    @Override
    public void fertilizerOff() {
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("dung, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }
    
}
