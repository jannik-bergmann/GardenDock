/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary;

import de.hsos.kbse.controller.ArduinoRepoImpl;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.SensorData;
import java.io.Serializable;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@ConversationScoped
@Named(value = "arduino")
public class ArduinoBoundary implements Serializable{

    @Inject
    ArduinoRepoImpl arduinoRepo;
    
    public void onClickDebug() {
        Arduino arduino = new Arduino();
        SensorData sensorData = new SensorData();
        sensorData.setAirhumidity(10);
        sensorData.setLightintensity(20);
        sensorData.setSoilhumidity(30);
        sensorData.setWaterlevel(40);
        
        arduino.setSensorData(sensorData);
        arduino.setIpAddress("1111");
        System.out.println(arduino.toString());
        arduinoRepo.newArduino(arduino);
        
        
        System.out.println("Ich bin eine DebugNachricht (:");
    }
}
