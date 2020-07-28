package de.hsos.kbse.dataListeners;

import de.hsos.kbse.entities.Arduino;

/** Interface for Datalisters
 *
 * @author Bastian Luehrs-Puellmann
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
