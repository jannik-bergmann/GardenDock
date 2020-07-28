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
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/** Repository for User CRUD Operation
 *
 * @author Bastian Luehrs-Puellmann
 */

public class UserRepository implements UserRepoInterface, Serializable {
    private EntityManagerFactory emf;
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
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        if(em.find(User.class, usr.getUserId()) != null) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public User updateUser(User usr) {
        if(usr == null) return null; 
        User toUpdate = em.find(User.class, usr.getUserId());
        em.getTransaction().begin();
        toUpdate.setPwdhash(usr.getPwdhash());
        toUpdate.setUsername(usr.getUsername());
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
        List<User> data = em.createQuery("SELECT h FROM User h" , User.class).getResultList();
        if(data.isEmpty()) return null;
        return data;
    }
    
    @Override
    public List<User> getArduinoUserByCredentials(Credentials credentials) {
        Query query = em.createQuery("from User where username=:username and pwdhash=:passwordhash");
        query.setParameter("username", credentials.getUsername());
        query.setParameter("passwordhash", credentials.getPassword());

        List<User> ArduinoUsers = null;
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
