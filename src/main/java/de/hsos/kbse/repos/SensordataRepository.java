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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.transaction.TransactionManager;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

@NoArgsConstructor
public class SensordataRepository implements SensordataRepoInterface, Serializable {  
    EntityManagerFactory emf;
    private EntityManager em;
    
    @PostConstruct
    private void init() {
        System.out.println("*************************************************Sensorrepo created");
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
        em.getTransaction().begin();
        List<Sensordata> data = em.createQuery("SELECT h FROM Sensordata h", Sensordata.class).getResultList();
        em.getTransaction().commit();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    public List<Sensordata> getLast100Entries() {
        em.getTransaction().begin();
        List<Sensordata> data = em.createQuery("select t from Sensordata t order by t.timeOfCapture desc", Sensordata.class)
                .setMaxResults(100)
                .getResultList();
        em.getTransaction().commit();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    public List<Sensordata> getLast100EntriesByArduino(Arduino arduino) {
        em.getTransaction().begin();
        List<Sensordata> data = em.createQuery("select t from Sensordata t where t.arduino=:arduino order by t.timeOfCapture desc", Sensordata.class)
                .setParameter("arduino", arduino)
                .setMaxResults(100)
                .getResultList();
        em.getTransaction().commit();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

}
