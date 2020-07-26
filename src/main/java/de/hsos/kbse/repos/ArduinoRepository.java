/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.repos;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import lombok.NoArgsConstructor;
import de.hsos.kbse.entities.User;

/**
 *
 * @author Basti's
 */

@NoArgsConstructor
public class ArduinoRepository implements ArduinoRepoInterface, Serializable {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @PostConstruct
    private void init() {
        try {
            emf = Persistence.createEntityManagerFactory("ogm-mongodb");
            em = emf.createEntityManager();
        } catch (PersistenceException ex) {
            System.err.println("********************************" + ex.toString());
        }
    }

    @PreDestroy
    private void cleanup() {
        emf.close();
        em.close();
    }

    // Arduino CRUD
    @Override
    public Arduino addArduino(Arduino ard) {
        em.getTransaction().begin();
        em.persist(ard);
        em.getTransaction().commit();
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        return temp;
    }
    
    @Override
    public int deleteArduino(Arduino ard) {
        if (ard != null) {
            em.getTransaction().begin();
            em.remove(ard);
            em.getTransaction().commit();
        }
        if (em.find(Arduino.class, ard.getArduinoId()) != null) {
            return 0;
        };
        return 1;
    }
  
    @Override
    public Arduino updateArduino(Arduino ard) {
        if (ard == null) {
            return null;
        }
        em.getTransaction().begin();
        em.persist(ard);
        em.getTransaction().commit();
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        if (temp == null) {
            return null;
        }
        return temp;
    }
    
    @Override
    public Arduino getArduino(String id) {
        Arduino ard = null;
        ard = em.find(Arduino.class, id);
        if (ard == null) {
            return null;
        }
        return ard;
    }
    
    @Override
    public List<Arduino> getAllArduino() {
        em.getTransaction().begin();
        List<Arduino> data = em.createQuery("SELECT h FROM Arduino h", Arduino.class).getResultList();
        em.getTransaction().commit();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    public List<Arduino> getAllArduinosByUser(User user) {
        em.getTransaction().begin();
        List<Arduino> data = em.createQuery("SELECT h FROM Arduino h where h.user=:user", Arduino.class)
                .setParameter("user", user)
                .getResultList();
        em.getTransaction().commit();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }
}
   
