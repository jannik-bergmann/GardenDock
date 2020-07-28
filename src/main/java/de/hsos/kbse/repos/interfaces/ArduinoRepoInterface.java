package de.hsos.kbse.repos.interfaces;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.User;
import java.util.List;
import javax.transaction.RollbackException;

/**
 *
<<<<<<< HEAD
 * @author Bastian Lührs-Püllmann
=======
 * @author Jannik Bergmann 
>>>>>>> d5b342fc64ced32b8087012d8fe20d3e2a43f3d6
 */

public interface ArduinoRepoInterface {
    public Arduino addArduino(Arduino ard);
    public int deleteArduino(Arduino ard);
    public Arduino updateArduino(Arduino ard)throws RollbackException;
    public Arduino getArduino(String id);
    public List<Arduino> getAllArduino();
    public List<Arduino> getAllArduinosByUser(User user);
}

