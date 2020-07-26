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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

    public void printSensorData100() {

        List<Sensordata> data = sensordataRepo.getLast100Entries();
        for (Sensordata sensordata : data) {
            System.out.println(sensordata.getSensorId());
            System.out.println(sensordata.getAirhumidity());
        }

    }

    public void printSensorData100ByArduino() {
        Arduino localArduino = arduinoRepo.getArduino("dfd44bef-09e9-4b02-9563-fd182c210fc1");

        List<Sensordata> data = sensordataRepo.getLast100EntriesByArduino(localArduino);

        data.forEach((sensordata) -> {
            System.out.println(">----------->"+sensordata.toString());
        });

    }

    public void printAllArduinosByUser() {
        User localUser = userRepo.getUser("2c84428e-aa36-404e-8bf1-ce86e7fab7b7");

        /*
        Arduino localArduino = arduinoRepo.getArduino("dfd44bef-09e9-4b02-9563-fd182c210fc1");
        System.out.println(">------------>"+localArduino.getArduinoId());
        System.out.println(">------------>"+localArduino.getComPort());
        System.out.println(">------------>"+localArduino.getName());
        //System.out.println(">------------>"+localArduino.toString());
        */
        
        
        
        List<Arduino> arduinos = arduinoRepo.getAllArduinosByUser(localUser);
        System.out.println(">------------->"+arduinos.get(0).toString());
        
        
        
        /*
        for(Arduino arduino: arduinos){
            System.out.println("ArduinoId:  "+arduino.getArduinoId());
        }
         */
    }
    
    public void sendFacesMessage(){
        FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Falscher Username oder Passwort",
                            "Bitte geben Sie gültige Nutzerdaten ein"));
    }
}
