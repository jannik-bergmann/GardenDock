package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import com.fazecast.jSerialComm.SerialPort;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lombok.NoArgsConstructor;

@GatewayModeSimulator
@NoArgsConstructor
public class IotGatewaySimulator implements IotGatewayInterface{
    
    // Repos
    @Inject
    ArduinoRepoInterface arduinoRepo;
    
    // Simulator
    private List<SimulatedIotConnection> openSimulatedConnections;
    
    // Other
    private boolean closed;
    
    @PostConstruct
    public void init() {
        // Get all Arduinos from DB and create IotSimulater
        this.openSimulatedConnections = new ArrayList<>();
        List<Arduino> arduinos = arduinoRepo.getAllArduino();
        for(Arduino ard : arduinos) {
            SimulatedIotConnection sdl = new SimulatedIotConnection(ard);
            if(sdl == null) continue;
            //sdl.routine();
            this.openSimulatedConnections.add(sdl);
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        for(SimulatedIotConnection con : openSimulatedConnections) {
            con.close();
        }
        this.closed = true;
    }
    
    private SimulatedIotConnection findConnection(String arduinoID) {
        // Get Arduino
        Arduino ard = arduinoRepo.getArduino(arduinoID);
        if(ard == null) return null;
        
        // Check if right IotConnection and return if correct
        for(SimulatedIotConnection con : this.openSimulatedConnections) {
            if(con.getArdId().equals(arduinoID)) {
                return con;
            }
        }
        
        return null;
    }
    
    @Override
    public void waterPumpOn(String arduinoID) {
        SimulatedIotConnection sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.waterOn();
    }

    @Override
    public void dungPumpOn(String arduinoID) {
        SimulatedIotConnection sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.fertilizerOn();
    }

    @Override
    public void waterPumpOff(String arduinoID) {
        SimulatedIotConnection sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.waterOff();
    }

    @Override
    public void dungPumpOff(String arduinoID) {
        SimulatedIotConnection sic = findConnection(arduinoID);
        if(sic == null) {
            System.err.println("Error while turning waterpump on: getting IotConnection of Arduino" + arduinoID);
            return;
        } 
        sic.fertilizerOff();
    }
   
}
