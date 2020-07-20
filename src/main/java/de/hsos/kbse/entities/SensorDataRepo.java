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
public interface SensorDataRepo {

    public void newSensorData(SensorData sensorData);

    public SensorData getSensorDataById(long id);

    public SensorData updateSensorData(SensorData sensorData);

    public void deleteSensorData(SensorData sensorData);
}
