/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.repos;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.transaction.TransactionManager;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

public class SensordataRepository implements SensordataRepoInterface, Serializable {  
    EntityManagerFactory emf;
    private EntityManager em;
    
    
    public SensordataRepository() {
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

    // Sensordata CRUD
    @Override
    public Sensordata addSensordata(Sensordata sd) {
        if (sd == null) {
            return null;
        }
        em.getTransaction().begin();
        em.persist(sd);
        em.getTransaction().commit();
        Sensordata temp = em.find(Sensordata.class, sd.getSensorId());
        return temp;
    }
    
    @Override
    public int deleteSensordata(Sensordata sd) {
        if (sd == null) {
            return 0;
        }
        em.getTransaction().begin();
        em.remove(sd);
        em.getTransaction().commit();
        if (em.find(Sensordata.class, sd.getSensorId()) != null) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public Sensordata updateSensordata(Sensordata sd) {
        if (sd == null) {
            return null;
        }
        em.getTransaction().begin();
        em.persist(sd);
        em.getTransaction().commit();
        Sensordata temp = em.find(Sensordata.class, sd.getSensorId());
        if (temp == null) {
            return null;
        }
        return temp;
    }
    
    @Override
    public Sensordata getSensordata(String id) {
        Sensordata sd = null;
        sd = em.find(Sensordata.class, id);
        if (sd == null) {
            return null;
        }
        return sd;
    }
    
    @Override
    public List<Sensordata> getAllSensordata() {
        List<Sensordata> data = em.createQuery("SELECT h FROM Sensordata h", Sensordata.class).getResultList();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    @Override
    public List<Sensordata> getLast100Entries() {
        List<Sensordata> data = em.createQuery("select t from Sensordata t order by t.timeOfCapture desc", Sensordata.class)
                .setMaxResults(100)
                .getResultList();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }
    
    @Override
    public Sensordata getLast(Arduino arduino) {
        Sensordata sd = null;
        try {
            sd = em.createQuery("select t from Sensordata t where t.arduino=:arduino order by t.timeOfCapture desc", Sensordata.class)
                .setParameter("arduino", arduino)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException ex) {
            System.err.println(ex.toString());
        }
        
        if(sd == null) return null;
        return sd;
    }
    
    @Override
    public List<Sensordata> getLast100EntriesByArduino(Arduino arduino) {
        List<Sensordata> data = em.createQuery("select t from Sensordata t where t.arduino=:arduino order by t.timeOfCapture desc", Sensordata.class)
                .setParameter("arduino", arduino)
                .setMaxResults(100)
                .getResultList();

        if (data.isEmpty()) {
            return null;
        }
        Collections.reverse(data);
        return data;
    }

}
