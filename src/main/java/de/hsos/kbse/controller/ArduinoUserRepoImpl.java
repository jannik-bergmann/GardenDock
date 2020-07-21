/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.controller;

import de.hsos.kbse.entities.ArduinoUser;
import de.hsos.kbse.entities.authorization.Credentials;
import de.hsos.kbse.entities.interfaces.ArduinoUserRepo;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@RequestScoped
@Transactional
public class ArduinoUserRepoImpl implements ArduinoUserRepo {

    @PersistenceContext(name = "GardenDockPU")
    private EntityManager em;

    @Override
    public void newArduinoUser(ArduinoUser arduinoUser) {
        this.em.persist(arduinoUser);
    }

    @Override
    public ArduinoUser getArduinoUserById(long id) {
        return this.em.find(ArduinoUser.class, id);
    }

    @Override
    public ArduinoUser updateArduinoUser(ArduinoUser arduinoUser) {
        return this.em.merge(arduinoUser);
    }

    @Override
    public void deleteArduinoUser(ArduinoUser arduinoUser) {
        ArduinoUser toMerge = this.em.merge(arduinoUser);
        this.em.remove(toMerge);
    }
    
    public List<ArduinoUser> getArduinoUserByCredentials(Credentials credentials) {

        Query query = em.createQuery("select u from ArduinoUser u where u.username=:username and u.password=:password" ,ArduinoUser.class);
        query.setParameter("username", credentials.getUsername());
        query.setParameter("password", credentials.getPassword());
        
        List<ArduinoUser> ArduinoUsers;
        try
        {
             ArduinoUsers = query.getResultList();
        }
        catch(NoResultException e){
            return null;
        }
        return ArduinoUsers;

    }

}
