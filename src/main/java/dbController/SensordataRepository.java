/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbController;

import entities.Sensordata;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 *
 * @author Basti's
 */

//@Transactional
public class SensordataRepository implements Serializable {
    private EntityManagerFactory emf;
    private TransactionManager tm;
    private EntityManager em;
    
    
    public SensordataRepository() {
        try {
            emf = Persistence.createEntityManagerFactory("ogm-mongodb");
            tm = (TransactionManager) com.arjuna.ats.jta.TransactionManager.transactionManager();
            em = emf.createEntityManager();
        } catch (PersistenceException ex) {
            System.err.println("********************************" + ex.toString());
        }
    }

    
    @PreDestroy
    public void cleanup() {
        emf.close();
        em.close();
    }
    
    // Helper functions for handling Transactions
    private void tmBegin() {
        try {
            tm.begin();
        } catch (NotSupportedException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void tmCommit() {
        try {
            tm.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Sensordata CRUD
    public Sensordata addSensordata(Sensordata sd) {
        tmBegin();
        if(sd == null) return null;
        em.persist(sd);
        Sensordata temp = em.find(Sensordata.class, sd.getSensorId());
        tmCommit();
        return temp;
    }
    
    public int deleteSensordata(Sensordata sd) {
        if(sd == null) return 0; 
        tmBegin();
        em.remove(sd);
        if(em.find(Sensordata.class, sd.getSensorId()) != null) {
            tmCommit();
            return 0;
        }
        tmCommit();
        return 1;
    }
    
    public Sensordata updateSensordata(Sensordata sd) {
        if(sd == null) return null; 
        tmBegin();
        em.persist(sd);
        Sensordata temp = em.find(Sensordata.class, sd.getSensorId());
        tmCommit();
        if(temp == null) return null;
        return temp;
    }
    
    public Sensordata getSensordata(String id) {
        Sensordata sd = null;
        tmBegin();
        sd = em.find(Sensordata.class, id);
        tmCommit();
        if(sd == null) { return null; }
        return sd;
    }
    
    public List<Sensordata> getAllSensordata() {
        tmBegin();
        List<Sensordata> data = em.createQuery("SELECT h FROM Sensordata h" , Sensordata.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
}
