package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */
import com.fazecast.jSerialComm.SerialPort;
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
    private List<DataListener> openSimulatedConnections;
    
    // Other
    private boolean closed;
    
    @PostConstruct
    public void init() {
        // Get all Arduinos from DB and create IotSimulater
        this.openSimulatedConnections = new ArrayList<>();
        
    }
    
    @Override
    public void startUp() {
        List<Arduino> arduinos = null;
        if((arduinos = arduinoRepo.getAllArduino())== null) {
            System.out.println("**************************** init error");
            return;
        }
        for(Arduino ard : arduinos) {
            DataListenerSimulated sdl = new DataListenerSimulated(ard, arduinoRepo, sensorRepo);
            if(sdl == null) continue;
            //sdl.routine();
            this.openSimulatedConnections.add(sdl);
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        openSimulatedConnections.forEach((con) -> {
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
        for(DataListener con : this.openSimulatedConnections) {
            if(con.getArdId().equals(arduinoID)) {
                return con;
            }
        }
        
        return null;
    }
    
    @Override
    public void waterPumpOn(String arduinoID) {
        DataListener sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.waterOn();
    }

    @Override
    public void dungPumpOn(String arduinoID) {
        DataListener sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.fertilizerOn();
    }

    @Override
    public void waterPumpOff(String arduinoID) {
        DataListener sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.waterOff();
    }

    @Override
    public void dungPumpOff(String arduinoID) {
        DataListener sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.fertilizerOff();
    }
   
}
