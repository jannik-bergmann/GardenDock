/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary;

import de.hsos.kbse.controller.ArduinoRepoImpl;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.SensorData;
import java.io.Serializable;
import javafx.scene.chart.PieChart;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@ConversationScoped
@Named(value = "arduino")
@Getter
@Setter
public class ArduinoBoundary implements Serializable {

    @Inject
    ArduinoRepoImpl arduinoRepo;

    private SensorData currentSensorData;
    
    private PieChartModel waterlevel;
    private PieChartModel soilhumidity;

    private BarChartModel barModel;

    @PostConstruct
    public void init() {
        //createWaterLevel();
        createBarModel();
        this.currentSensorData = new SensorData(0,0,0,0,0);
    }

    private void createPieModels() {
        createWaterLevel();
    }

    private void updateDisplayedData(SensorData sensorData){
        
    }
    
    private void createBarModel() {
        barModel = initBarModel();

        barModel.setTitle("Füllstände");
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        //xAxis.setLabel("Gender");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);
    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();

        ChartSeries waterlevel = new ChartSeries();
        waterlevel.setLabel("Wasserfüllstand");
        waterlevel.set("", 30);
        

        ChartSeries soilhumidity = new ChartSeries();
        soilhumidity.setLabel("Bodenfeuchtigkeit");
        soilhumidity.set("", 52);
        
        ChartSeries airhumidity = new ChartSeries();
        airhumidity.setLabel("Bodenfeuchtigkeit");
        airhumidity.set("", 72);
        
        ChartSeries lightintensity = new ChartSeries();
        lightintensity.setLabel("Bodenfeuchtigkeit");
        lightintensity.set("", 92);

        model.addSeries(waterlevel);
        model.addSeries(soilhumidity);
        model.addSeries(airhumidity);
        model.addSeries(lightintensity);
        

        return model;
    }

    private void createSoilHumidity() {
        soilhumidity = new PieChartModel();

    }

    private void createWaterLevel() {
        waterlevel = new PieChartModel();

        waterlevel.set("Tanke gefüllt", 50);
        waterlevel.set("Tank leer", 10);

        waterlevel.setTitle("Wasserfüllstand");
        waterlevel.setShowDataLabels(false);
        //pieModel1.setLegendPosition("w");
        waterlevel.setShadow(false);
    }

    public void onClickDebug() {
        Arduino arduino = new Arduino();
        SensorData sensorData = new SensorData();
        sensorData.setAirhumidity(10);
        sensorData.setLightintensity(20);
        sensorData.setSoilhumidity(30);
        sensorData.setWaterlevel(40);
        sensorData.setTemperature(23.5);

        this.currentSensorData = sensorData;
        
        arduino.setSensorData(sensorData);
        
        arduino.setIpAddress("1111");
        System.out.println(arduino.toString());
        arduinoRepo.newArduino(arduino);

        System.out.println("Ich bin eine DebugNachricht (:");
    }
}
