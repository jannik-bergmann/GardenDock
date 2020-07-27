package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import de.hsos.kbse.dataListeners.DataListenerArduino;
import de.hsos.kbse.dataListeners.DataListener;
import com.fazecast.jSerialComm.SerialPort;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
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

@GatewayModeArduino
@NoArgsConstructor
@ManagedBean
public class IotGatewayArduino implements IotGatewayInterface { 
    
    // Repos
    @Inject
    ArduinoRepoInterface arduinoRepo;
    
    // Arduinos
    private List<DataListener> openConnections;
    
    @PostConstruct
    public void init() {
        this.openConnections = new ArrayList<>();
    }
    
    @Override
    public void startUp() {
    List<Arduino> arduinos = arduinoRepo.getAllArduino();
        for(Arduino ard : arduinos) {
            SerialPort sp = null;
            sp = SerialPort.getCommPort(ard.getComPort());
            if(!sp.openPort()) {
                System.err.println("Error while opening port for Arduino " + ard.getName());
                continue;
            }
            sp.setComPortParameters(9600, 8, 1, 0);
            sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
            
            DataListenerArduino ardDataListener = new DataListenerArduino();
            if(ardDataListener == null) {
                System.err.println("Error while creating ArduinoDataListener for Arduino " + ard.getName());
                continue;
            }
            ardDataListener.setArduino(ard);
            ardDataListener.setSerialPort(sp);
            
            sp.addDataListener(ardDataListener);

            this.openConnections.add(ardDataListener);
        }
    }
    
    @Override
    public DataListener findConnection(String arduinoID) {
        // Get Arduino
        Arduino ard = arduinoRepo.getArduino(arduinoID);
        if(ard == null) return null;
        
        // Check if right IotConnection and return if correct
        for(DataListener con : this.openConnections) {
            if(con.getArdId().equals(arduinoID)) {
                return con;
            }
        }
        
        return null;
    }

    @PreDestroy
    @Override
    public void cleanup() {
        this.openConnections.forEach((dl) -> {
            dl.close();
        });
    }
    
    // React to new and altered Arduinos
    @PostRemove
    private void afterRemove(Arduino ard) {
        DataListener dl = findConnection(ard.getArduinoId());
        dl.close();
        this.openConnections.remove(dl);
        System.out.println("Arduino " + ard.getArduinoId() + " deleted");
    }
    
    @PostPersist
    private void afterNew(Arduino ard) {
        System.out.println("h*******************************************************************i");
        SerialPort sp = null;
        sp = SerialPort.getCommPort(ard.getComPort());
        if (!sp.openPort()) {
            System.err.println("Error while opening port for Arduino " + ard.getName());
            return;
        }
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        DataListenerArduino ardDataListener = new DataListenerArduino();

        ardDataListener.setArduino(ard);
        ardDataListener.setSerialPort(sp);

        sp.addDataListener(ardDataListener);

        this.openConnections.add(ardDataListener);
        System.out.println("Arduino " + ard.getArduinoId() + " added");
    }
    
    @PostUpdate
    private void afterUpdate(Arduino ard) {
        DataListener dl = findConnection(ard.getArduinoId());
        dl.setArduino(ard);
        System.out.println("Arduino " + ard.getArduinoId() + " updated");
    }
    
   
    /*** Arduino Functions
     * @param arduinoID ***/
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
