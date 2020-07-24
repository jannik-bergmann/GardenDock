/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */
public interface IotGatewayInterface {
    void init();
    void cleanup();
    void routine();
    void waterPumoOn();
    void dungPumpOn();
    void waterPumoOff();
    void dungPumpOff();
}
