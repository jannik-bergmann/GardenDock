package de.hsos.kbse.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.NoArgsConstructor;

/** Interface fot Boundry classes
 *
 * @author Bastian Lührs-Püllmann
 */

@NoArgsConstructor
@ApplicationScoped
public class App {
    @Inject
    StreamAnalytics server;
    
    /**
     * Starting Point of the backend
     * @param init        observer for Applicationscope init
     */
    private void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws InterruptedException {
 
    }
    
    
    /**
     * Turn on the waterpump
     * @param arduinoID    id of the Arduino
     */
    public void waterPumpOn(String arduinoID) {
        server.waterPumpOn(arduinoID);
    }
    
    /**
     * Turn on the fertilizerpump
     * @param arduinoID    id of the Arduino
     */
    public void fertilizerPumpOn(String arduinoID) {
        server.fertilizerPumpOn( arduinoID);
    }
    
    /**
     * Turn off the waterpump
     * @param arduinoID    id of the Arduino
     */
    public void waterPumpOff(String arduinoID) {
        server.waterPumpOff(arduinoID);
    }
    
    /**
     * Turn off the fertilizer
     * @param arduinoID    id of the Arduino
     */
    public void fertilizerPumpOff(String arduinoID) {
        server.fertilizerPumpOff(arduinoID);
    }

}
