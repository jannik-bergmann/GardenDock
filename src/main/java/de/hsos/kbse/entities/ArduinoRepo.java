/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
public interface ArduinoRepo {
    public void newArduino(Arduino arduino);
    public Arduino getArduinoById(long id);
    public Arduino updateArduino(Arduino arduino);
    public void deleteArduino(Arduino arduino);
}
