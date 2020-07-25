/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.debug;

import de.hsos.kbse.controller.ArduinoRepository;
import de.hsos.kbse.controller.SensordataRepository;
import de.hsos.kbse.controller.UserRepository;
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
    
    public void persistSensorData(){
        Sensordata sensordata = new Sensordata(0,10,20,30,40,50);
        sensordataRepo.addSensordata(sensordata);
        
    }
    
    public void persistArduino(){
        Arduino ard = new Arduino();
        User temp = new User();
        userRepo.addUser(temp);
        ard.setUser(temp);
        ard.setComPort("ComPortBeispiel");
        ard.setName("ArduinoEins");
        arduinoRepo.addArduino(ard);
    }
}
