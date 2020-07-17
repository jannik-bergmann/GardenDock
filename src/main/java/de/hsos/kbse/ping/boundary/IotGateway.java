/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.ping.boundary;


import java.io.IOException;
import com.fazecast.jSerialComm.SerialPort;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author bastianluhrspullmann
 */
public class IotGateway {
    private SerialPort sp;
    
    public IotGateway() {
        this.sp = SerialPort.getCommPort("/dev/cu.usbmodem.14301");
        sp.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
    }
    
    @PostConstruct
    void init() {
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }        
        
        try {
            for (Integer i = 0; i < 5; ++i) {            
                sp.getOutputStream().write(i.byteValue());
                sp.getOutputStream().flush();
                System.out.println("Sent number: " + i);

                    Thread.sleep(1000);

            }   
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        if (sp.closePort()) {
            System.out.println("Port is closed :)");
        } else {
            System.out.println("Failed to close port :(");
            return;
        }
    }
    
}
