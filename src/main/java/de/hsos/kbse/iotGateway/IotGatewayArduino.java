package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import de.hsos.kbse.entities.Sensordata;
import java.io.IOException;
import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ManagedBean
@GatewayModeArduino
public class IotGatewayArduino implements IotGatewayInterface { 
    // Arduino
    private final SerialPort sp;
    ArduinoDataListener arduinoListener;
    List<Sensordata> sensordata = new ArrayList<>();
    // Jms
    private InputStream input;
    private OutputStream output;
    private IotSimulator simulator;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    // Scheduler 
    ScheduledExecutorService scheduler;
    
    // Constructor
    public IotGatewayArduino () {
        // Scheduler 
        scheduler = Executors.newScheduledThreadPool( 1 );
        // Arduino connection
        this.sp = SerialPort.getCommPort("/dev/tty.usbmodem14301");
        if(sp.openPort()) {
            sp.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
            sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
            input = sp.getInputStream();
            output = sp.getOutputStream();
        }
        this.arduinoListener = new ArduinoDataListener(sp);
         
        System.out.println("IotGateway erstellt");
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
            
        } catch (NamingException | JMSException  ex) {
            System.err.println(ex.toString());
        }
   
        // Setup serial arduino connection
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }
        
        sp.addDataListener(arduinoListener);
    }

    
    @PreDestroy
    @Override
    public void cleanup() {
        // Close serial arduino connection
        if (sp.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
        }
        
        try {
            // Close jms connection     
            this.connection.close();
            this.session.close();
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewayArduino.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void sendData() {
        try {
            // TODO: parse Sensordata to JMS Message
            TextMessage message = session.createTextMessage();
            message.setText("Wasserstand: 20%");     
            producer.send(message);
        } catch (JMSException ex) {
            Logger.getLogger(IotGatewayArduino.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void getData() {
        Sensordata temp = arduinoListener.getSensordata();
        if(temp != null) this.sensordata.add(temp);
    }
    
    @Override
    public void routine() {
        System.out.println("123");
        // jms & arduino
        /* 
            - alle 5 Sekunden Daten auslesen vom arduino oder aus Simulatorklasse
            - diese dann per jms an das topic "sensordata" schicken
        
            - auf Nachrichten vom topic "actuators" warten
            - diese ggf. ausführen
        */
        
        scheduler.scheduleAtFixedRate(
            new Runnable() {
                @Override
                public void run() {
                    // Gt last data from rduino
                    getData();
                    
                    // Send data via jms
                    sendData();
                }    
            }, 1, 2, TimeUnit.SECONDS   
        );
        
    }
    
    /*** Arduino Functions ***/
    @Override
    public void waterPumoOn() {
        try {         
            output.write(Byte.parseByte("water, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void dungPumpOn() {
        try {         
            output.write(Byte.parseByte("dung, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void waterPumoOff() {
        try {         
            output.write(Byte.parseByte("water, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void dungPumpOff() {
        try {         
            output.write(Byte.parseByte("dung, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void startUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
