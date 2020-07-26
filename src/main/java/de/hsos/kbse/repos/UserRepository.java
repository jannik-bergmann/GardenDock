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
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

public class UserRepository implements UserRepoInterface, Serializable {
    EntityManagerFactory emf;
    private EntityManager em;
    
    public UserRepository() {
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

    // User CRUD
    @Override
    public User addUser(User usr) {
        if(usr == null) return null;
        em.getTransaction().begin();
        em.persist(usr);
        em.getTransaction().commit();
        User temp = em.find(User.class, usr.getUserId());
        return temp;
    }
    
    @Override
    public int deleteUser(User usr) {
        if(usr == null) return 0; 
        em.getTransaction().begin();
        em.remove(usr);
        em.getTransaction().commit();
        if(em.find(User.class, usr.getUserId()) != null) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public User updateUser(User usr) {
        if(usr == null) return null; 
        em.getTransaction().begin();
        em.persist(usr);
        em.getTransaction().commit();
        User temp = em.find(User.class, usr.getUserId());
        if(temp == null) return null;
        return temp;
    }
    
    @Override
    public User getUser(String id) {
        User usr = null;
        usr = em.find(User.class, id);
        if(usr == null) { return null; }
        return usr;
    }
    
    @Override
    public List<User> getAllUser() {
        em.getTransaction().begin();
        List<User> data = em.createQuery("SELECT h FROM User h" , User.class).getResultList();
        em.getTransaction().commit();
        if(data.isEmpty()) return null;
        return data;
    }
    
    @Override
    public List<User> getArduinoUserByCredentials(Credentials credentials) {
        em.getTransaction().begin();
        Query query = em.createQuery("select u from User u where u.username=:username and u.pwdhash=:passwordhash" ,User.class);
        em.getTransaction().commit();
        query.setParameter("username", credentials.getUsername());
        query.setParameter("passwordhash", credentials.getPassword());

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
