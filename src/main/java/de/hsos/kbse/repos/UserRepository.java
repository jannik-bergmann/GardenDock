/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.repos;

import de.hsos.kbse.entities.User;
import de.hsos.kbse.entities.authorization.Credentials;
import de.hsos.kbse.repos.interfaces.UserRepoInterface;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.TransactionManager;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

//@TransactionManagement(TransactionManagementType.BEAN)
@NoArgsConstructor
public class UserRepository implements UserRepoInterface, Serializable {
    //@PersistenceContext(unitName = "ogm-mongodb")
    EntityManagerFactory emf;
    //private TransactionManager tm;
    private EntityManager em;
    
    @PostConstruct
    private void init() {
        System.out.println("*************************************************Sensorrepo created");
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
            tm.begin();
        } catch (NotSupportedException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
    }
    
    private void tmCommit() {
  /*      try {
            tm.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            Logger.getLogger(SensordataRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
    }
   
    // User CRUD
    @Override
    public User addUser(User usr) {
        if(usr == null) return null;
        em.persist(usr);
        User temp = em.find(User.class, usr.getUserId());
        return temp;
    }
    
    @Override
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
    
    @Override
    public User updateUser(User usr) {
        if(usr == null) return null; 
        tmBegin();
        em.persist(usr);
        User temp = em.find(User.class, usr.getUserId());
        tmCommit();
        if(temp == null) return null;
        return temp;
    }
    
    @Override
    public User getUser(String id) {
        tmBegin();
        User usr = null;
        usr = em.find(User.class, id);
        tmCommit();
        if(usr == null) { return null; }
        return usr;
    }
    
    @Override
    public List<User> getAllUser() {
        tmBegin();
        List<User> data = em.createQuery("SELECT h FROM User h" , User.class).getResultList();
        tmCommit();
        if(data.isEmpty()) return null;
        return data;
    }
    
    @Override
    public List<User> getArduinoUserByCredentials(Credentials credentials) {

        tmBegin();
        
        Query query = em.createQuery("select u from User u where u.username=:username and u.pwdhash=:passwordhash" ,User.class);
        query.setParameter("username", credentials.getUsername());
        query.setParameter("passwordhash", credentials.getPassword());
        
        tmCommit();
        
        
        List<User> ArduinoUsers;
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
