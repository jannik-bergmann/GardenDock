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
import entities.User;
import java.io.Serializable;
import java.util.Date;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ConsumerMessageListener implements MessageListener, Serializable {
    private SensordataRepository sensorRepo;
    private ArduinoRepository ardRepo;
    private UserRepository userRepo;

    private String consumerName;

    public ConsumerMessageListener(String consumerName) {
        this.sensorRepo = new SensordataRepository();
        this.ardRepo = new ArduinoRepository();
        this.userRepo = new UserRepository();
        this.consumerName = consumerName;
    }
    
    public ConsumerMessageListener() {
        this.sensorRepo = new SensordataRepository();
        this.ardRepo = new ArduinoRepository();
        this.userRepo = new UserRepository();
    }
     
    public void persistSensorData(String data) {
        System.out.println("********************** persist: " + data);
        String[] data_splitted = data.split(",");
        Arduino arduino = new Arduino();
        arduino.setName("asdjkas");
        arduino.setComPort("asga");
        User userTest = new User();
        userTest.setUsername("asf");
        userTest.setPwdhash("hash");

        userRepo.addUser(userTest);
        arduino.setUser(userTest);
        ardRepo.addArduino(arduino);

        Sensordata sd = new Sensordata();
        if (data_splitted.length != 7) {
            return;
        }
        // Validate watermeter

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
        sd.setArduino(arduino);

        sensorRepo.addSensordata(sd);
    }

    @Override
    public void onMessage(Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            System.out.println(consumerName + " received " + textMessage.getText());
            persistSensorData(textMessage.getText());
        } catch (JMSException e) {
            System.err.println("Error while getting JMS Textmessage" + e.toString());

        }
    }

}
