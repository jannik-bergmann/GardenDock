package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
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
    private List<SimulatorDataListener> simulatorDatalisteners;
    
    // Scheduler
    private boolean closed;
    
    @PostConstruct
    public void init() {
        // Get all Arduinos from DB and create IotSimulater
        System.out.println("Gateway init");
        this.simulatorDatalisteners = new ArrayList<>();
        List<Arduino> arduinos = arduinoRepo.getAllArduino();
        for(Arduino ard : arduinos) {
            SimulatorDataListener sdl = new SimulatorDataListener(ard);
            if(sdl == null) continue;
            //sdl.routine();
            this.simulatorDatalisteners.add(sdl);
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        this.closed = true;
    }
     
    @Override
    public void startUp() {
        System.out.println("IotGateway erstellt");
        closed = false;
    }
    
    @Override
    public void waterPumpOn() {
        System.out.println("Waterpump on");
    }

    @Override
    public void dungPumpOn() {
        System.out.println("Fertelizerpump on");
    }

    @Override
    public void waterPumpOff() {
        System.out.println("Waterpump off");
    }

    @Override
    public void dungPumpOff() {
        System.out.println("Fertelizerpump off");
    }
   
}
