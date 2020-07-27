/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.dataListeners;

import de.hsos.kbse.entities.Arduino;

/**
 *
 * @author Basti's
 */
public interface DataListener {
    public void waterOn();
    public void fertilizerOn();
    public void waterOff();
    public void fertilizerOff();
    public String getArdId();
    public void close();
    public void currentToSetValue();
    public void setArduino(Arduino ard);
}
