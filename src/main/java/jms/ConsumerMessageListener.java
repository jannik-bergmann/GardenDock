package jms;

/**
 *
 * @author bastianluhrspullmann
 */

import dbController.ArduinoRepository;
import dbController.SensordataRepository;
import dbController.UserRepository;
import entities.Arduino;
import entities.Sensordata;
import java.util.Date;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.validation.constraints.Max;

public class ConsumerMessageListener implements MessageListener {
    //@Inject
    //private SensordataRepository sensorRepo;
    //@Inject
    //private ArduinoRepository ardRepo;

    private final String consumerName;
    
    public ConsumerMessageListener(String consumerName) {
        this.consumerName = consumerName;
    }
    
    private void persistSensorData(String data) {
        String[] data_splitted = data.split(",");
        Sensordata sd = new Sensordata();
        if(data_splitted.length != 7) return;
        // Validate watermeter
        System.out.println("********************** persist: " + data);
        //Arduino ardToInsert = ardRepo.getArduino("18cbde73-db3e-4f6b-bbc4-be8769d574f7");
        //System.out.println(ardToInsert.getArduinoId());
        //if(ardToInsert == null) { System.out.println("ard was null"); return; }
        sd.setWaterMeter(Integer.parseInt(data_splitted[0]));
        sd.setDungMeter(Integer.parseInt(data_splitted[1]));
        sd.setSunLevel(Integer.parseInt(data_splitted[2]));
        sd.setAirHumidity(Integer.parseInt(data_splitted[3]));
        sd.setSoilHumidity(Integer.parseInt(data_splitted[4]));
        sd.setTemperature(Integer.parseInt(data_splitted[5]));
        sd.setTimestamp(new Date());
        //sd.setArduino(ardToInsert);
        
        //sensorRepo.addSensordata(sd);
    }
    
    @Override
    public void onMessage(Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            System.out.println(consumerName + " received " + textMessage.getText());
            persistSensorData(textMessage.getText());
        } catch (JMSException e) {  
            System.err.println("Error while getting JMS Textmessage");
            e.toString();
        }
    }

}
