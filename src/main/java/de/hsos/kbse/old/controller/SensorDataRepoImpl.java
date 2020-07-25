/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package de.hsos.kbse.old.controller;

import de.hsos.kbse.old.entities.SensorData;
import de.hsos.kbse.entities.interfaces.SensorDataRepo;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
*/
/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */

/*
@RequestScoped
@Transactional
public class SensorDataRepoImpl implements SensorDataRepo {

    @PersistenceContext(name = "GardenDockPU")
    private EntityManager em;

    @Override
    public void newSensorData(SensorData sensorData) {
        this.em.persist(sensorData);
    }

    @Override
    public SensorData getSensorDataById(long id) {
        return this.em.find(SensorData.class, id);
    }

    @Override
    public SensorData updateSensorData(SensorData sensorData) {
        return this.em.merge(sensorData);
    }

    @Override
    public void deleteSensorData(SensorData sensorData) {
        SensorData toMerge = this.em.merge(sensorData);
        this.em.remove(toMerge);
    }

}
*/