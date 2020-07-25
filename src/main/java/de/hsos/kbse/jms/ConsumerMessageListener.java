package de.hsos.kbse.jms;

/**
 *
 * @author bastianluhrspullmann
 */
import de.hsos.kbse.controller.ArduinoRepository;
import de.hsos.kbse.controller.SensordataRepository;
import de.hsos.kbse.controller.UserRepository;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.entities.User;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener, Serializable {
    private SensordataRepository sensorRepo;
    private ArduinoRepository ardRepo;
    private UserRepository userRepo;

    private String consumerName;

    public ConsumerMessageListener(String consumerName) { 
        
        this.sensorRepo = new SensordataRepository();
        this.ardRepo = new ArduinoRepository();
        this.userRepo = new UserRepository();

        this.consumerName = consumerName;
    }
    
    public ConsumerMessageListener() {
        this.sensorRepo = new SensordataRepository();
        this.ardRepo = new ArduinoRepository();
        this.userRepo = new UserRepository();

    }
     
    public void persistSensorData(MapMessage data) {
        try {
            System.out.println("********************** persist: " + data);
            Sensordata sd = new Sensordata();
            
            Arduino ardToInsert = ardRepo.getArduino(data.getString("arduinoId"));
            if(ardToInsert == null) { System.out.println("ard was null"); return; }
            sd.setWaterlevel(data.getInt("waterMeter"));
            sd.setFertilizerlevel(data.getInt("dungMeter"));
            sd.setLightintensity(data.getInt("sunLevel"));
            sd.setAirhumidity(data.getInt("airHumidity"));
            sd.setSoilhumidity(data.getInt("soilHumidity"));
            sd.setTemperature(data.getDouble("temperature"));
            sd.setTimeOfCapture(new Date());
            sd.setArduino(ardToInsert);
            
            sensorRepo.addSensordata(sd);
        } catch (JMSException ex) {
            Logger.getLogger(ConsumerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message msg) {
        System.out.println(consumerName + " received " + msg.toString());
        MapMessage message = (MapMessage) msg;
        persistSensorData(message);
    }

}