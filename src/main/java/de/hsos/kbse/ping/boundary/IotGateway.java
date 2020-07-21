/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.ping.boundary;


import java.io.IOException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;


/**
 *
 * @author bastianluhrspullmann
 */
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
@ManagedBean
public class IotGateway {
    private SerialPort sp;
    private InputStream input;
    private OutputStream output;
    private String topicname = "sensordata";
    
    public IotGateway() {
        System.out.println("IotGateway erstellt");
        this.sp = SerialPort.getCommPort("/dev/tty.usbmodem14301");
        if(sp.openPort()) {
            sp.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
            sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
            input = sp.getInputStream();
            output = sp.getOutputStream();
        }
    }
    
    @PostConstruct
    void init() {
        
        // Setup serial arduino connection
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
        }

        sp.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;           
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
               if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
               byte[] data = new byte[sp.bytesAvailable()];
               int input = sp.readBytes(data, data.length);
               System.out.println("Input: " + data.toString());
            }
        });     
        
        // Setup jms connection as sender
        try {
            // Build up connection
            InitialContext context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            Connection connection = topicFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false -> not transacted, AUTO_ACKNOWLEDGE -> automatisch Nachrichten entgegennehmen
            TextMessage message = session.createTextMessage();
            message.setText("Wasserstand: 20%");
            Topic topic = (Topic) context.lookup("jms.Topic");
            connection.start();
            MessageProducer producer = session.createProducer(topic);
     
            
            // Send Message
            System.out.println("send message");
            producer.send(message);     
            
            Thread.sleep(3000);
            
            producer.send(message);
            
            // Close jms connection
            // connection.stop();
            connection.close();
            session.close();
        } catch (NamingException | JMSException | InterruptedException ex) {
            System.err.println(ex.toString());
        }
        
        
        
        // Close serial arduino connection
        if (sp.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }
    }
    
    private void activateWater() {
        try {         
            output.write(Byte.parseByte("water"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }
    
    private void activateDung() {
        try {         
            output.write(Byte.parseByte("dung"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }
    
    private int getWaterMeter() {
        int val = 0;
        return val;
    }
    
    private int getDungMeter() {
        int val = 0;
        return val;
    }
    
    private int getSunlevel() {
        int val = 0;
        return val;
    }
    
    private int getSoilhumidity() {
        int val = 0;
        return val;
    }
    
    private int getAirhumidity() {
        int val = 0;
        return val;
    }
    
    private int getTemperature() {
        int val = 0;
        return val;
    }
    
    
}
