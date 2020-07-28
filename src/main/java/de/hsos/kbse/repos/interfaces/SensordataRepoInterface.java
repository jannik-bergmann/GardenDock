package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import java.util.List;



 /* @author Bastian Luehrs-Puellmann,  Jannik Bergmann 
 */

public interface SensordataRepoInterface {
    public Sensordata addSensordata(Sensordata sd);
    public int deleteSensordata(Sensordata sd); 
    public Sensordata updateSensordata(Sensordata sd);
    public Sensordata getSensordata(String id);
    public List<Sensordata> getAllSensordata();
    public Sensordata getLast(Arduino arduino);
    public List<Sensordata> getLast100Entries();
    public List<Sensordata> getLast100EntriesByArduino(Arduino arduino);
}
