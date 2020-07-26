package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import java.io.IOException;
import com.fazecast.jSerialComm.SerialPort;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lombok.NoArgsConstructor;

@GatewayModeArduino
@NoArgsConstructor
public class IotGatewayArduino implements IotGatewayInterface { 
    
    // Repos
    @Inject
    ArduinoRepoInterface arduinoRepo;
    
    // Arduinos
    private List<SerialPort> openSerialPorts;
    
    @PostConstruct
    public void init() {
        this.openSerialPorts = new ArrayList<>();
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
            
            ArduinoDataListener ardDataListener = new ArduinoDataListener(sp);
            if(ardDataListener == null) {
                System.err.println("Error while creating ArduinoDataListener for Arduino " + ard.getName());
                continue;
            }
            sp.addDataListener(ardDataListener);
            
            this.openSerialPorts.add(sp);
        }

    }
    
    private SerialPort findSerialport(String ardID) {
        // Get Arduino
        Arduino ard = arduinoRepo.getArduino(ardID);
        if(ard == null) return null;
        
        // Check if right SerialPort and return if correct
        for(SerialPort port : this.openSerialPorts) {
            if(port.getDescriptivePortName().equals(ard.getComPort())) {
                return port;
            }
        }
        
        return null;
    }

    @PreDestroy
    @Override
    public void cleanup() {
        // Close serial arduino connections
        for(SerialPort sp : openSerialPorts) {
            if (sp.closePort()) {
                System.out.println("Port " + sp.toString() + "is closed :)");
            } else {
                System.out.println("Failed to close port :(");
            }
        }
    }
   
    /*** Arduino Functions
     * @param arduinoID ***/
    @Override
    public void waterPumpOn(String arduinoID) {
        OutputStream output = null;
        SerialPort sp = findSerialport(arduinoID);
        if(sp == null) {
            System.err.println("Error while turning waterpump on: getting SerialPort of Arduino " + arduinoID);
            return;
        }
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("water, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void dungPumpOn(String arduinoID) {
        OutputStream output = null;
        SerialPort sp = findSerialport(arduinoID);
        if(sp == null) {
            System.err.println("Error while turning fertilizerpump on: getting SerialPort of Arduino " + arduinoID);
            return;
        }
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("dung, on"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void waterPumpOff(String arduinoID) {
        OutputStream output = null;
        SerialPort sp = findSerialport(arduinoID);
        if(sp == null) {
            System.err.println("Error while turning waterpump off: getting SerialPort of Arduino " + arduinoID);
            return;
        }
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("water, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

    @Override
    public void dungPumpOff(String arduinoID) {
        OutputStream output = null;
        SerialPort sp = findSerialport(arduinoID);
        if(sp == null) {
            System.err.println("Error while turning fertilizerpump off: getting SerialPort of Arduino " + arduinoID);
            return;
        }
        output = sp.getOutputStream();
        
        try {         
            output.write(Byte.parseByte("dung, off"));
            output.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }  
    }

}
