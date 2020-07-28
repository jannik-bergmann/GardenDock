/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package de.hsos.kbse.boundary;

import de.hsos.kbse.controller.SensorDataRepoImpl;
import de.hsos.kbse.entities.SensorData;
import de.hsos.kbse.entities.interfaces.SensorDataRepo;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
*/
/**
 *
 * @author Jannik Bergmann 
 */
/*
@ApplicationScoped
public class IoTDeviceGatewayMock {

    @Inject
    SensorDataRepo sensorDataRepo;

    @PostConstruct
    private void init() {
        receiveData();
    }

    public void receiveData() {
        System.out.println("Die Daten werden erhalten ");

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable yourRunnable = new Runnable() {
            @Override
            public void run() {

                SensorData sensorData = new SensorData(
                        getRandomIntegerBetweenRange(0, 100),
                        getRandomIntegerBetweenRange(0, 100),
                        getRandomIntegerBetweenRange(0, 100),
                        getRandomIntegerBetweenRange(0, 100),
                        getRandomIntegerBetweenRange(0, 100),
                        getRandomIntegerBetweenRange(0, 100)
                );
                sensorDataRepo.newSensorData(sensorData);
                System.out.println("Neue SensorData: " + sensorData);

            }
        };

        int initialDelay = 0;
        int delay = 5;
        scheduler.scheduleWithFixedDelay(yourRunnable, initialDelay, delay, TimeUnit.SECONDS);
    }
    
    public static int getRandomIntegerBetweenRange(double min, double max) {
        int x = (int) ((int) (Math.random() * ((max - min) + 1)) + min);
        return x;
    }

}
*/