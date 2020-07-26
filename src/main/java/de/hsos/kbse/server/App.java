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
    
    private void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("test");
    }
    
    public void waterPumpOn() {
        server.waterPumpOn();
    }
    
    public void fertilizerPumpOn() {
        server.fertilizerPumpOn();
    }
    
    public void waterPumpOff() {
        server.waterPumpOff();
    }
    
    public void fertilizerPumpOff() {
        server.fertilizerPumpOff();
    }
    
}
