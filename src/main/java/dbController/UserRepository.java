/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbController;

import entities.User;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@RequestScoped
@TransactionManagement(TransactionManagementType.BEAN)
public class UserRepository implements Serializable {
    private EntityManagerFactory emf;
    private TransactionManager tm;
    private EntityManager em;
    
    public UserRepository() {
        System.out.println("*************************************************Userrepo created");
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
   
    // User CRUD
    public User addUser(User usr) {
        if(usr == null) return null;
        tmBegin();
        em.persist(usr);
        User temp = em.find(User.class, usr.getUserId());
        tmCommit();
        return temp;
    }
    
    public int deleteUser(User usr) {
        if(usr == null) return 0; 
        tmBegin();
        em.remove(usr);
        if(em.find(User.class, usr.getUserId()) != null) {
            tmCommit();
            return 0;
        }
        tmCommit();
        return 1;
    }
    
    public User updateUser(User usr) {
        if(usr == null) return null; 
        tmBegin();
        em.persist(usr);
        User temp = em.find(User.class, usr.getUserId());
        tmCommit();
        if(temp == null) return null;
        return temp;
    }
    
    public User getUser(String id) {
        tmBegin();
        User usr = null;
        usr = em.find(User.class, id);
        tmCommit();
        if(usr == null) { return null; }
        return usr;
    }
    
    public List<User> getAllUser() {
        tmBegin();
        List<User> data = em.createQuery("SELECT h FROM User h" , User.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
}
