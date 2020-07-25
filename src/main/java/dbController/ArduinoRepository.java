/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbController;

import entities.Arduino;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.RequestScoped;
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
/*@RequestScoped*/
@TransactionManagement(TransactionManagementType.BEAN)
public class ArduinoRepository implements Serializable {
    private EntityManagerFactory emf;
    private TransactionManager tm;
    private EntityManager em;
    
    public ArduinoRepository() {
        System.out.println("*************************************************ArduinoRepo created");
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
    
    // Arduino CRUD
    public Arduino addArduino(Arduino ard) {
        System.out.println("add Arduino" + ard.getArduinoId());
        if(ard == null) return null;
        tmBegin();
        em.persist(ard);
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        tmCommit();
        return temp;
    }
    
    public int deleteArduino(Arduino ard) {
        tmBegin();
        if(ard != null) em.remove(ard);
        tmCommit();
        tmBegin();
        if(em.find(Arduino.class, ard.getArduinoId()) != null) {
            return 0;
        }
        tmCommit();
        return 1;
    }
    
    public Arduino updateArduino(Arduino ard) {
        if(ard == null) return null; 
        tmBegin();
        em.persist(ard);
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        tmCommit();
        if(temp == null) return null;
        return temp;
    }
    
    public Arduino getArduino(String id) {
        System.out.println("123124");
        tmBegin();
        Arduino ard = null;
        ard = em.find(Arduino.class, id);
        tmCommit();
        if(ard == null) { return null; }
        return ard;
    }
    
    public List<Arduino> getAllArduino() {
        tmBegin();
        List<Arduino> data = em.createQuery("SELECT h FROM Arduino h" , Arduino.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
}
