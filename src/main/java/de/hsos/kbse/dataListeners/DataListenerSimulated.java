package de.hsos.kbse.dataListeners;

/**
 *
 * @author bastianluhrspullmann
 */

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
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
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
import javax.persistence.Persistence;

@Named
@RequestScoped
public class DataListenerSimulated implements DataListener {
    // Jms
    private TopicSession session;
    private TopicPublisher producer;
    private TopicPublisher emptyWarner;
    
    // Scheduler
    private ScheduledExecutorService scheduler;
    private int[] lastValues;
    
    // Others
    private Random rand;
    private Arduino arduino;
    
    // Repos
    private SensordataRepoInterface sensordataRepo;
    private ArduinoRepoInterface arduinoRepo;

    public DataListenerSimulated() {
        rand = new Random();
        
        // Repos 
        this.sensordataRepo = new SensordataRepository();
        this.arduinoRepo = new ArduinoRepository();
 
        // Init scheduler
        scheduler = Executors.newScheduledThreadPool( 1 );
        
        // Init JMS publisher        
        System.out.println("cunstructor datalistener");
        try {
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            TopicConnection con = topicFactory.createTopicConnection();
            con.start();
            session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topicProducer = (Topic) context.lookup("jms.Topic");
            producer = session.createPublisher(topicProducer);
            
            //Topic topicWarnings = (Topic) context.lookup("jms.Warning");
            //emptyWarner = session.createPublisher(topicWarnings);
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(DataListenerSimulated.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    public void init() {
        // Init 'lastValues' for Simulation
        System.out.println("listener init");
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
            for(int val : lastValues) {
                val = 0;
            }
        }
        this.routine();
    }
    
    public void setArduino(Arduino ard) {
        this.arduino = ard;
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
        System.out.println("Send data" + values_split.toString());
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
    
    /* Values[]:
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
        soilHum -= rand.nextInt(2);
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
    
    // Scheduler for generating sensordata und produce messages for topic
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
    
    // Routine for automatically water and fertilize the bed of the arduino
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
    
    // Stop scheduler
    @Override
    public void close() {
        scheduler.shutdownNow();
        System.out.println("Port " + arduino.getComPort() + " closed :)");
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
        }
        this.lastValues[0] -= 1;
        
        if(this.lastValues[4] > 95) this.lastValues[4] = 100;
        this.lastValues[4] += 4;
        
        System.out.println("Arduino " + this.arduino.getArduinoId() + ": Waterpump on");
    }
    
    @Override
    public void fertilizerOn() {
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
