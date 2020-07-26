/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */
public interface IotGatewayInterface {
    void cleanup();
    void waterPumpOn();
    void dungPumpOn();
    void waterPumpOff();
    void dungPumpOff();
    void startUp();
}
