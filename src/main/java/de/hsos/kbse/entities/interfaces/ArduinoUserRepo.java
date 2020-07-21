/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities.interfaces;

import de.hsos.kbse.entities.ArduinoUser;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
public interface ArduinoUserRepo {

    public void newArduinoUser(ArduinoUser arduinoUser);

    public ArduinoUser getArduinoUserById(long id);

    public ArduinoUser updateArduinoUser(ArduinoUser arduinoUser);

    public void deleteArduinoUser(ArduinoUser arduinoUser);
}
