package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.User;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
package de.hsos.kbse.entities.interfaces;

import de.hsos.kbse.old.entites.Arduino;
*/
/**
 *
 * @author Jannik Bergmann 
 */

public interface ArduinoRepoInterface {
    public Arduino addArduino(Arduino ard);
    public int deleteArduino(Arduino ard);
    public Arduino updateArduino(Arduino ard);
    public Arduino getArduino(String id);
    public List<Arduino> getAllArduino();
    public List<Arduino> getAllArduinosByUser(User user);
}

