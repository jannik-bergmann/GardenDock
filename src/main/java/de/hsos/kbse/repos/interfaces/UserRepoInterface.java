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
<<<<<<< HEAD
 * @author Bastian L�hrs-P�llmann
=======
 * @author Jannik Bergmann 
>>>>>>> d5b342fc64ced32b8087012d8fe20d3e2a43f3d6
 */

public interface UserRepoInterface {
    public User addUser(User usr);
    public int deleteUser(User usr);
    public User updateUser(User usr);
    public User getUser(String id);
    public List<User> getAllUser();
    public List<User> getArduinoUserByCredentials(Credentials credentials);           
}

