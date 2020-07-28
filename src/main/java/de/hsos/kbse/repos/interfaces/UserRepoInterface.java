package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.User;
import de.hsos.kbse.entities.authorization.Credentials;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package de.hsos.kbse.entities.interfaces;

import de.hsos.kbse.old.entites.ArduinoUser;
*/
/**
 *
 * @author Bastian Lührs-Püllmann
 */

public interface UserRepoInterface {
    public User addUser(User usr);
    public int deleteUser(User usr);
    public User updateUser(User usr);
    public User getUser(String id);
    public List<User> getAllUser();
    public List<User> getArduinoUserByCredentials(Credentials credentials);           
}

