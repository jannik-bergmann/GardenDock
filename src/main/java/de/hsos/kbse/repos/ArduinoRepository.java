/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.repos;

import com.arjuna.ats.jta.transaction.Transaction;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.repos.interfaces.ArduinoRepoInterface;
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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
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

//Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
@NoArgsConstructor
public class ArduinoRepository implements ArduinoRepoInterface, Serializable {
    private EntityManagerFactory emf;
    //@PersistenceContext(unitName = "ogm-mongodb")
    //TransactionManager tm;
    private EntityManager em;

    @PostConstruct
    private void init() {
        try {
            emf = Persistence.createEntityManagerFactory("ogm-mongodb");
            //tm = (TransactionManager) com.arjuna.ats.jta.TransactionManager.transactionManager();
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
    
    // Arduino CRUD
    @Override
    public Arduino addArduino(Arduino ard) {
        if (ard == null) {
            return null;
        }
        em.getTransaction().begin();
        em.persist(ard);
        em.getTransaction().commit();
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        return temp;
    }
    
    @Override
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
    
    @Override
    public Arduino updateArduino(Arduino ard) {
        if(ard == null) return null; 
        tmBegin();
        em.persist(ard);
        Arduino temp = em.find(Arduino.class, ard.getArduinoId());
        tmCommit();
        if(temp == null) return null;
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
        tmBegin();
        List<Arduino> data = em.createQuery("SELECT h FROM Arduino h" , Arduino.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
}
