package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */
import de.hsos.kbse.dataListeners.DataListenerSimulated;
import de.hsos.kbse.dataListeners.DataListener;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import lombok.NoArgsConstructor;

@GatewayModeSimulator
@NoArgsConstructor
@ManagedBean
public class IotGatewaySimulator implements IotGatewayInterface {
    
    // Repos
    @Inject
    ArduinoRepoInterface arduinoRepo;
    @Inject
    SensordataRepoInterface sensorRepo;
    
    // Simulator
    private List<DataListener> dataListeners;
    
    // Other
    private boolean closed;
    
    @PostConstruct
    public void init() {
        // Get all Arduinos from DB and create IotSimulater
        this.dataListeners = new ArrayList<>();
        
    }
    
    @Override
    public void startUp() {
        List<Arduino> arduinos = null;
        if((arduinos = arduinoRepo.getAllArduino())== null) {
            System.out.println("**************************** init error");
            return;
        }
        for(Arduino ard : arduinos) {
            DataListenerSimulated sdl = new DataListenerSimulated();
            if(sdl == null) {
                System.err.println("Error while creating SimulatedDataListener for Arduino " + ard.getName());
                continue;
            }
            sdl.setArduino(ard);
            sdl.init();
            this.dataListeners.add(sdl);
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        dataListeners.forEach((con) -> {
            con.close();
        });
        this.closed = true;
    }
    
    @Override
    public DataListener findConnection(String arduinoID) {
        // Get Arduino
        Arduino ard = arduinoRepo.getArduino(arduinoID);
        if(ard == null) return null;
        
        // Check if right IotConnection and return if correct
        for(DataListener con : this.dataListeners) {
            if(con.getArdId().equals(arduinoID)) {
                return con;
            }
        }
        return null;
    }
    
    
    // React to new and altered Arduinos
    @PostRemove
    private void afterRemove(Arduino ard) {
        DataListener dl = findConnection(ard.getArduinoId());
        dl.close();
        this.dataListeners.remove(dl);
        System.out.println("ard Removed");
    }
    
    @PostPersist
    private void afterNew(Arduino ard) {
        
        DataListenerSimulated sdl = new DataListenerSimulated();
        if (sdl == null) {
            System.err.println("Error while creating ArduinoDataListener for Arduino " + ard.getName());
            return;
        }
        sdl.setArduino(ard);
        sdl.init();
        this.dataListeners.add(sdl);

        System.out.println("Arduino " + ard.getArduinoId() + " added");
    }
    
    @PostUpdate
    private void afterUpdate(Arduino ard) {
                System.out.println("h*******************************************************************i");
        DataListener dl = findConnection(ard.getArduinoId());
        dl.setArduino(ard);
        System.out.println("Arduino " + ard.getArduinoId() + " updated");
    }
    
    // Water and Fertilizerpump
    @Override
    public void waterPumpOn(String arduinoID) {
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.waterOn();
    }

    @Override
    public void dungPumpOn(String arduinoID) {
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.fertilizerOn();
    }

    @Override
    public void waterPumpOff(String arduinoID) {
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.waterOff();
    }

    @Override
    public void dungPumpOff(String arduinoID) {
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.fertilizerOff();
    }
   
}
