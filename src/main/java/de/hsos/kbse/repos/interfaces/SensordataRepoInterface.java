package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.Sensordata;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package de.hsos.kbse.entities.interfaces;

import de.hsos.kbse.old.entities.SensorData;
*/
/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */

public interface SensordataRepoInterface {
    public Sensordata addSensordata(Sensordata sd);
    public int deleteSensordata(Sensordata sd); 
    public Sensordata updateSensordata(Sensordata sd);
    public Sensordata getSensordata(String id);
    public List<Sensordata> getAllSensordata();
}