package de.hsos.kbse.iotGateway;

/**
 *
 * @author bastianluhrspullmann
 */

import java.util.Random;

/*
* Data: [temperature, air humidity, soil humidity, sun intensity, water meter, dung meter]
*/
public class IotSimulator {
    private Random rand;

    public IotSimulator() {
        rand = new Random();
    }
    
    /* lastValues[]:
        *int waterMeter;
        *int dungMeter;
        *int sunLevel;
        *int airHumidity;
        *int soilHumidity;
        *int temperature;
    */
    public String generateSensordata(int[] lastValues) {
        String csv = "";
        boolean initLastValues = true;
        for(int val : lastValues) {
            if(val != 0) initLastValues = false;
        }
        
        // WaterMeter
        int waterMeter = lastValues[0];
        if(initLastValues) { waterMeter = rand.nextInt(101); }
        waterMeter += rand.nextInt(10)-5;
        if(waterMeter < 0) waterMeter = 0;
        if(waterMeter > 100) waterMeter = 100;
        
        // DungMeter
        int dungMeter = lastValues[1];
        if(initLastValues) { dungMeter = rand.nextInt(101); }
        dungMeter += rand.nextInt(10)-5;
        if(dungMeter < 0) dungMeter = 0;
        if(dungMeter > 100) dungMeter = 100;
        
        // SunLevel
        int sunLevel = lastValues[2];
        if(initLastValues) { sunLevel = rand.nextInt(101); }
        sunLevel += rand.nextInt(20)-10;
        if(sunLevel < 0) sunLevel = 0;
        if(sunLevel > 100) sunLevel = 100;
        
        // AirHumidity
        int airHum = lastValues[3];
        if(initLastValues) { airHum = rand.nextInt(101); }
        airHum += rand.nextInt(4)-2;
        if(airHum < 0) airHum = 0;
        if(airHum > 100) airHum = 100;
        
        // SoilHumidity
        int soilHum = lastValues[4];
        if(initLastValues) { soilHum = rand.nextInt(101); }
        soilHum += rand.nextInt(2)-1;
        if(soilHum < 0) soilHum = 0;
        if(soilHum > 100) soilHum = 100;
        
        // Temperature
        int temp = lastValues[5];
        if(initLastValues) { temp = rand.nextInt(30); }
        temp += rand.nextInt(2)-1;
        if(temp < 0) temp = 0;
        if(temp > 30) temp = 30;
        
        // Build CSV
        csv = String.format("%d,%d,%d,%d,%d,%d", waterMeter, dungMeter, sunLevel, airHum, soilHum, temp);
        
        return csv;
    }
    
    
}
