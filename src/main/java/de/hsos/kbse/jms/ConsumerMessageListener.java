package de.hsos.kbse.jms;

/**
 *
 * @author bastianluhrspullmann
 */
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import de.hsos.kbse.repos.interfaces.UserRepoInterface;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ConsumerMessageListener implements MessageListener, Serializable {
    @Inject
    private SensordataRepoInterface sensorRepo;
    @Inject
    private ArduinoRepoInterface ardRepo;
    @Inject
    private UserRepoInterface userRepo;

    private String consumerName;

    public ConsumerMessageListener(String consumerName) { 
        this.consumerName = consumerName;
    }
    
    public ConsumerMessageListener() {
        this.consumerName = "";
    }
     
    public void persistSensorData(MapMessage data) {
        try {
            System.out.println("********************** persist: " + data);
            Sensordata sd = new Sensordata();
            
            Arduino ardToInsert = ardRepo.getArduino(data.getString("arduinoId"));
            if(ardToInsert == null) { 
                System.out.println("ard was null"); 
                sd = null;
                return; 
            }

            sd.setWaterlevel(data.getInt("waterMeter"));
            sd.setFertilizerlevel(data.getInt("dungMeter"));
            sd.setLightintensity(data.getInt("sunLevel"));
            sd.setAirhumidity(data.getInt("airHumidity"));
            sd.setSoilhumidity(data.getInt("soilHumidity"));
            sd.setTemperature(data.getDouble("temperature"));
            sd.setTimeOfCapture(LocalDateTime.now());
            sd.setArduino(ardToInsert);
            
            sensorRepo.addSensordata(sd);
        } catch (JMSException ex) {
            Logger.getLogger(ConsumerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            msg.acknowledge();
        } catch (JMSException ex) {
            Logger.getLogger(ConsumerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(consumerName + " received " + msg.toString());
        MapMessage message = (MapMessage) msg;
        persistSensorData(message);
    }

}
