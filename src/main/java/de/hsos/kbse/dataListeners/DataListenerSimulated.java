package de.hsos.kbse.dataListeners;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.iotGateway.IotGatewaySimulator;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.SensordataRepository;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
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
import javax.transaction.RollbackException;

/** Simulating incoming data and parse them to JSM Messages
 *
 * @author Bastian Luehrs-Puellmann
 */

@Named
@RequestScoped
public class DataListenerSimulated implements DataListener {
    // Jms
    private TopicSession session;
    private TopicConnection con;
    private TopicPublisher producer;
    private TopicPublisher emptyWarner;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    private int[] lastValues;
    
    // Others
    private Random rand;
    private Arduino arduino;
    private boolean closed;
    
    // Repos
    private SensordataRepoInterface sensordataRepo;
    private ArduinoRepoInterface arduinoRepo;

    /** Init scheduler and jms connection 
    */
    public DataListenerSimulated() {
        rand = new Random();
        closed = false;
        
        // Repos 
        this.sensordataRepo = new SensordataRepository();
        this.arduinoRepo = new ArduinoRepository();
 
        // Scheduler
        scheduler = Executors.newScheduledThreadPool( 1 );
        
        // JMS      
        try {
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            con = topicFactory.createTopicConnection();
            con.start();
            Topic topicProducer = (Topic) context.lookup("jms.Topic");
            Topic topicWarnings = (Topic) context.lookup("jms.Warning");
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createPublisher(topicProducer);
            emptyWarner = session.createPublisher(topicWarnings);
            
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    /** Init simulated Sensordata and start routine
    */
    public void init() {
        // Init 'lastValues' for Simulation
        Sensordata lastData = sensordataRepo.getLast(arduino);
        if(lastData != null) { 
            lastValues = new int[6];
            lastValues[0] = lastData.getWaterlevel();
            lastValues[1] = lastData.getFertilizerlevel();
            lastValues[2] = lastData.getLightintensity();
            lastValues[3] = lastData.getAirhumidity();
            lastValues[4] = lastData.getSoilhumidity();
            lastValues[5] = (int)lastData.getTemperature();
        } else {
            lastValues = new int[6];
            lastValues[0] = 80;
            lastValues[1] = 72;
            lastValues[2] = 40;
            lastValues[3] = 75;
            lastValues[4] = 90;
            lastValues[5] = 25;
        }
        this.routine();
    }
    
    /** Send jms message to topic emptyTankWarning
      * @param tank    indicates if water or fertilizer tank is empty
    */
    private void sendTankEmptyWarning(String tank) {
        if(tank.equals("water")) {
            try {
                TextMessage msg = session.createTextMessage();
                msg.setText("Watertank empty");
                emptyWarner.publish(msg);
            } catch (JMSException ex) {
                Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                System.err.println("Error while creating JMS Message");
                Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(tank.equals("fertilizer")) {
            try {
                TextMessage msg = session.createTextMessage();
                msg.setText("Fertilizertank empty");
                emptyWarner.publish(msg);
            } catch (JMSException ex) {
                Logger.getLogger(DataListenerArduino.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                System.err.println("Error while creating JMS Message");
                Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /** Send jms message to topic emptyTankWarning
      * @param values _split    values from simulater
    */
    private void sendMessage(String[] values_split) {
        if(closed) return;
        try {
            MapMessage msg = session.createMapMessage();
            msg.setInt("waterMeter", Integer.parseInt(values_split[0]));
            msg.setInt("dungMeter", Integer.parseInt(values_split[1]));
            msg.setInt("sunLevel", Integer.parseInt(values_split[2]));
            msg.setInt("airHumidity", Integer.parseInt(values_split[3]));
            msg.setInt("soilHumidity", Integer.parseInt(values_split[4]));
            msg.setDouble("temperature", Double.parseDouble(values_split[5]));
            msg.setString("arduinoId", values_split[6]);
            producer.publish(msg);
        } catch (JMSException ex ) {
            System.err.println("Error while creating JMS Message");
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            System.err.println("Error while creating JMS Message");
            Logger.getLogger(IotGatewaySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* 
    */
    /** Send jms message to topic emptyTankWarning
      * @param lastValues   last simulated values 
      * lastValues[]:
        *int waterMeter;
        *int dungMeter;
        *int sunLevel;
        *int airHumidity;
        *int soilHumidity;
        *int temperature;   
    */
    private String generateSensordata(int[] lastValues) {
        String csv = "";
        boolean initLastValues = true;
        for(int val : lastValues) {
            if(val != 0) initLastValues = false;
        }
        
        // WaterMeter
        int waterMeter = lastValues[0];
        if(initLastValues) { waterMeter = 50 + rand.nextInt(50); }
        
        // DungMeter
        int dungMeter = lastValues[1];
        if(initLastValues) { dungMeter = 50 + rand.nextInt(50); }
        
        // SunLevel
        int sunLevel = lastValues[2];
        if(initLastValues) { sunLevel = rand.nextInt(101); }
        sunLevel += rand.nextInt(10)-5;
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
        soilHum -= rand.nextInt(2);
        if(soilHum < 0) soilHum = 0;
        if(soilHum > 100) soilHum = 100;
        
        // Temperature
        int temp = lastValues[5];
        if(initLastValues) { temp = rand.nextInt(30); }
        temp += rand.nextInt(2)-1;
        if(temp <-20) temp = -20;
        if(temp > 40) temp = 40;
        
        // Build CSV
        csv = String.format("%d,%d,%d,%d,%d,%d", waterMeter, dungMeter, sunLevel, airHum, soilHum, temp);
        
        return csv;
    }
    
    /** Scheduler for generating sensordata und produce messages for topic
    */
    private void routine() {
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
            currentToSetValue();
            
            // sende Message
            sendMessage(values_split);
        }, 1, 5, TimeUnit.SECONDS   
        );
    }
    
    /** Routine for automatically water and fertilize the bed of the arduino
    */
    @Override
    public void currentToSetValue() {
        int currentSoilhumidity = lastValues[4];
        // Check soilhumidity
        if(arduino.getSetWaterLevel() > currentSoilhumidity) {
            // Water
            this.waterOn();
            this.waterOff();
        }
        
        // Check timer for fertilizing
        LocalDateTime from = LocalDateTime.from(arduino.getLastFertilization());
        long daysSinceLastFert = from.until(LocalDateTime.now(), ChronoUnit.DAYS);
        if(daysSinceLastFert > Long.valueOf(arduino.getFertilizerIntervallInDays())) {
            // Fertilizer
            this.fertilizerOn();
            this.fertilizerOff();
        }
        
    }
    
    /** Stop scheduler
    */
    @Override
    public void close() {
        this.scheduler.shutdownNow();
        this.closed = true;
        try {
            this.session.close();
            this.session = null;
            con.close();
        } catch (JMSException ex) {
            Logger.getLogger(DataListenerSimulated.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Port " + arduino.getComPort() + " closed :)");
    }
     
    @Override
    public void setArduino(Arduino ard) {
        this.arduino = ard;
    }
    
    @Override
    public String getArdId() {
        return this.arduino.getArduinoId();
    }
    
    @Override
    public void waterOn() {
        if(this.lastValues[0] == 0) {
            this.lastValues[0] = 0;
            sendTankEmptyWarning("water");
        } else {
            this.lastValues[0] -= 1;
        }

        if(this.lastValues[4] > 95) this.lastValues[4] = 100;
        this.lastValues[4] += 4;
        
        System.out.println("Arduino " + this.arduino.getArduinoId() + ": Waterpump on");
    }
    
    @Override
    public void fertilizerOn() {
        try {
            if(this.lastValues[1] == 0) {
                this.lastValues[1] = 0;
                sendTankEmptyWarning("fertilizer");
            }
            this.lastValues[1] -= 2;
            
            // Update Ferilization date in DB
            Arduino ardTemp = arduinoRepo.getArduino(arduino.getArduinoId());
            ardTemp.setLastFertilization(LocalDateTime.now());
            arduinoRepo.updateArduino(ardTemp);
            System.out.println("Arduino " + this.arduino.getArduinoId() + ": Fertilizerpump on");
        } catch (RollbackException ex) {
            Logger.getLogger(DataListenerSimulated.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void waterOff() {
        System.out.println("Arduino " + this.arduino.getArduinoId() + ": Waterpump off");
    }
    
    @Override
    public void fertilizerOff() {
        System.out.println("Arduino " + this.arduino.getArduinoId() + ": Fertilizerpump off");
    }
}
