/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.controller;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.interfaces.ArduinoRepo;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@RequestScoped
@Transactional
public class ArduinoRepoImpl implements ArduinoRepo{

    
    @PersistenceContext(name = "GardenDockPU")
    private EntityManager em;
    
    @Override
    public void newArduino(Arduino arduino) {
        this.em.persist(arduino);
    }

    @Override
    public Arduino getArduinoById(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Arduino updateArduino(Arduino arduino) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteArduino(Arduino arduino) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
