/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.debug;

import de.hsos.kbse.boundary.ArduinoBoundary;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.SensordataRepository;
import de.hsos.kbse.repos.UserRepository;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.entities.User;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@Getter
@Setter
@Named
@ApplicationScoped
public class DebugBoundary {

    @Inject
    ArduinoRepository arduinoRepo;
    @Inject
    UserRepository userRepo;
    @Inject
    SensordataRepository sensordataRepo;
    
    Arduino arduino;
    User user;

    public void persistSensorData() {
       
        
            Sensordata sensordata = new Sensordata(
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 100),
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 100),
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 100),
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 100),
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 100),
                    ArduinoBoundary.getRandomIntegerBetweenRange(0, 39)
            );
            sensordata.setArduino(arduino);
            
            sensordataRepo.addSensordata(sensordata);
        

    }

    public void persistArduino() {
        arduino = new Arduino();
        user = new User();
        user.setUsername("admin");
        user.setPwdhash("admin");
        userRepo.addUser(user);
        arduino.setUser(user);
        arduino.setComPort("ComPortBeispiel");
        arduino.setName("ArduinoEins");
        arduinoRepo.addArduino(arduino);
    }
}
