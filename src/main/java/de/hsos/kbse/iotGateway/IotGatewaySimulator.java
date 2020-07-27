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
    
    public IotGatewaySimulator() {
        this.dataListeners = new ArrayList<>();
    }
    
    @Override
    public void startUp() {
        List<Arduino> arduinos = new ArrayList<>();
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
        for(DataListener dl : this.dataListeners) {
            dl.close();
        }
        this.closed = true;
    }
    
    @Override
    public DataListener findConnection(String arduinoID) {
        // Get Arduino
        if(this.arduinoRepo == null) { return null; }
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
        
        if(this.dataListeners == null) return;
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
        if(this.dataListeners == null) return;
        System.out.println("h*******************************************************************i");
        if(ard == null) return;   
        DataListener dl = findConnection(ard.getArduinoId());
        if(dl == null) return;
        dl.setArduino(ard);
        System.out.println("Arduino " + ard.getArduinoId() + " updated");

    }
    
    // Water and Fertilizerpump
    @Override
    public void waterPumpOn(String arduinoID) {
        if(this.dataListeners == null) return;
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.waterOn();
    }

    @Override
    public void fertilizerPumpOn(String arduinoID) {
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
    public void fertilizerPumpOff(String arduinoID) {
        DataListener dl = findConnection(arduinoID);
        if(dl == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        dl.fertilizerOff();
    }
   
}
