package de.hsos.kbse.iotGateway;

import de.hsos.kbse.dataListeners.DataListener;

/** Interface for IotGateways
 *
 * @author Bastian Luehrs-Puellmann
 */
public interface IotGatewayInterface {
    void cleanup();
    void waterPumpOn(String arduinoID);
    void fertilizerPumpOn(String arduinoID);
    void waterPumpOff(String arduinoID);
    void fertilizerPumpOff(String arduinoID);
    void startUp();
    DataListener findConnection(String ardID);
}
