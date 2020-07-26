/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.iotGateway;

import de.hsos.kbse.dataListeners.DataListener;

/**
 *
 * @author bastianluhrspullmann
 */
public interface IotGatewayInterface {
    void cleanup();
    void waterPumpOn(String arduinoID);
    void dungPumpOn(String arduinoID);
    void waterPumpOff(String arduinoID);
    void dungPumpOff(String arduinoID);
    void startUp();
    DataListener findConnection(String ardID);
}
