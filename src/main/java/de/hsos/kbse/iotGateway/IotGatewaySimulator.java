package de.hsos.kbse.iotGateway;

import de.hsos.kbse.dataListeners.DataListenerSimulated;
import de.hsos.kbse.dataListeners.DataListener;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/** Manages the iot connections
 *
 * @author Bastian Luehrs-Puellmann
 */

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
    
    /** create datalister for every arduino
    */
    @Override
    public void startUp() {
        List<Arduino> arduinos = new ArrayList<>();
        if((arduinos = arduinoRepo.getAllArduino())== null) {
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
    
    /** call close() method in all DL and close them
    */
    @PreDestroy
    @Override
    public void cleanup() {
        for(DataListener dl : this.dataListeners) {
            dl.close();
        }
        this.closed = true;
    }
    
     /** Search for Datalister with specific arduino
      * @param arduinoID    ID of the arduino the DL holds
      * @return             datalister with arduino or null
    */
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
    
    
    /** React to new arduinos --> new Datalistener
      * @param ard    new arduino
    */
    @PostRemove
    private void afterRemove(Arduino ard) {
        
        DataListener dl = null;
        dl = findConnection(ard.getArduinoId());
        if(dl == null) return;
        dl.close();
        this.dataListeners.remove(dl);
        System.out.println("ard Removed");

    }
    
    /** React to deleted arduinos --> delete Datalistener
      * @param ard    new arduino
    */
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
    
    /** React to updated arduinos --> update Datalistener with arduino
      * @param ard    new arduino
    */
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
    
    /** find datalistender with specific aruduino and start waterpump
      * @param arduinoID    ID of the arduino the DL holds
    */
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
