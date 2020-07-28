package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.User;
import java.util.List;
import javax.transaction.RollbackException;

/**
 *
 * @author Bastian Luehrs-Puellmann,  Jannik Bergmann
*/

public interface ArduinoRepoInterface {
    public Arduino addArduino(Arduino ard);
    public int deleteArduino(Arduino ard);
    public Arduino updateArduino(Arduino ard)throws RollbackException;
    public Arduino getArduino(String id);
    public List<Arduino> getAllArduino();
    public List<Arduino> getAllArduinosByUser(User user);
}

