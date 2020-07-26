/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.repos;

import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.repos.interfaces.SensordataRepoInterface;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

//@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
@NoArgsConstructor
public class SensordataRepository implements SensordataRepoInterface, Serializable {  
    EntityManagerFactory emf;
    TransactionManager tm;
    //@PersistenceContext(unitName = "ogm-mongodb")
    private EntityManager em;
    
    @PostConstruct
    private void init() {
        System.out.println("*************************************************Sensorrepo created");
        try {
            emf = Persistence.createEntityManagerFactory("ogm-mongodb");
            tm = (TransactionManager) com.arjuna.ats.jta.TransactionManager.transactionManager();
            em = emf.createEntityManager();
        } catch (PersistenceException ex) {
            System.err.println("********************************" + ex.toString());
        }
    }

    @PreDestroy
    private void cleanup() {
        //emf.close();
        //em.close();
    }
    
    // Helper functions for handling Transactions
    private void tmBegin() {
        /*
        try {
            tm = com.arjuna.ats.jta.TransactionManager.transactionManager(); 
            tm.begin();
        } catch (NotSupportedException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
    }
    
    private void tmCommit() {
        /*
        try {
            tm.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
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
    
    @Override
    public Sensordata updateSensordata(Sensordata sd) {
        if(sd == null) return null; 
        tmBegin();
        em.persist(sd);
        Sensordata temp = em.find(Sensordata.class, sd.getSensorId());
        tmCommit();
        if(temp == null) return null;
        return temp;
    }
    
    @Override
    public Sensordata getSensordata(String id) {
        Sensordata sd = null;
        tmBegin();
        sd = em.find(Sensordata.class, id);
        tmCommit();
        if(sd == null) { return null; }
        return sd;
    }
    
    @Override
    public List<Sensordata> getAllSensordata() {
        tmBegin();
        List<Sensordata> data = em.createQuery("SELECT h FROM Sensordata h" , Sensordata.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
}
