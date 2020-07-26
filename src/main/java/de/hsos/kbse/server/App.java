/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.NoArgsConstructor;

/**
 *
 * @author Basti's
 */

@NoArgsConstructor
@ApplicationScoped
public class App {
    @Inject
    StreamAnalytics server;
    
    private void init(@Observes @Initialized(ApplicationScoped.class) Object init) throws InterruptedException {
        System.out.println("test");
        
        /* // Test PumpON / PumpOff 
        String arduinoID = "2f778d37-5e9a-4564-a0ff-5a088f99b558";
        this.waterPumpOn(arduinoID);
        Thread.sleep(1000);
        this.waterPumpOff(arduinoID);
        this.fertilizerPumpOn(arduinoID);
        Thread.sleep(1000);
        this.fertilizerPumpOff(arduinoID);
        */
    }
    
    public void waterPumpOn(String arduinoID) {
        server.waterPumpOn(arduinoID);
    }
    
    public void fertilizerPumpOn(String arduinoID) {
        server.fertilizerPumpOn( arduinoID);
    }
    
    public void waterPumpOff(String arduinoID) {
        server.waterPumpOff(arduinoID);
    }
    
    public void fertilizerPumpOff(String arduinoID) {
        server.fertilizerPumpOff(arduinoID);
    }
    
    
    
}
