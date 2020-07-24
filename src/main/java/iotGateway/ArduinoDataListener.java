/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotGateway;

import entities.Sensordata;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 *
 * @author bastianluhrspullmann
 */
public class ArduinoDataListener implements SerialPortDataListener {
    private Sensordata sensordata;
    private SerialPort sp;
    
    public ArduinoDataListener(SerialPort sp) {
        this.sp = sp;
        sensordata = new Sensordata();
    }
    
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;           
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
        byte[] data = new byte[sp.bytesAvailable()];
        int input = sp.readBytes(data, data.length);
        System.out.println("Input: " + data.toString());
        // TODO: parse input to sensordata
    }
    
    Sensordata getSensordata() {
        return this.sensordata;
    }
    
}
