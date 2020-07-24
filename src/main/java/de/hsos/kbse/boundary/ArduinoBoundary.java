/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary;

import static de.hsos.kbse.boundary.IoTDeviceGatewayMock.getRandomIntegerBetweenRange;
import de.hsos.kbse.controller.ArduinoRepoImpl;
import de.hsos.kbse.controller.ArduinoUserRepoImpl;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.ArduinoUser;
import de.hsos.kbse.entities.SensorData;
import de.hsos.kbse.entities.interfaces.SensorDataRepo;
import de.hsos.kbse.util.ChartUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@ViewScoped
@Named(value = "arduino")
@Getter
@Setter
public class ArduinoBoundary implements Serializable {

    @Inject
    ArduinoRepoImpl arduinoRepo;

    @Inject
    SensorDataRepo sensorDataRepo;

    @Inject
    ArduinoUserRepoImpl arduinoUserRepo;

    private String page;

    private SensorData currentSensorData;

    private PieChartModel waterlevel;
    private PieChartModel soilhumidity;

    private LineChartModel waterlevelLineModel;
    private LineChartModel soilmoistureLineModel;
    private LineChartModel airhumidityLineModel;
    private LineChartModel lightintensityLineModel;
    private LineChartModel temperatureLineModel;

    private BarChartModel barModel;

    List<SensorData> sensorDataCollection;

    @PostConstruct
    public void init() {
        page = "landing";
        createBarModel();
        this.currentSensorData = new SensorData(0, 0, 0, 0, 0);
        sensorDataCollection = new ArrayList();
        initLineModels();
    }

    private void initLineModels() {
        waterlevelLineModel = ChartUtil.drawWaterlevelChart(sensorDataCollection);
        soilmoistureLineModel = ChartUtil.drawSoilMoistureChart(sensorDataCollection);
        airhumidityLineModel = ChartUtil.drawAirhumidityChart(sensorDataCollection);
        lightintensityLineModel = ChartUtil.drawLightintensityChart(sensorDataCollection);
        temperatureLineModel = ChartUtil.drawTemperatureChart(sensorDataCollection);

    }

    private void updateDisplayedData(SensorData sensorData) {

    }

    private LineChartModel initLineChartWaterlevel() {
        LineChartModel model = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("waterlevel");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getWaterlevel());
        });

        model.addSeries(waterlevelSeries);

        return model;
    }

    public void drawWaterlevelChart() {
        waterlevelLineModel = initLineChartWaterlevel();
        waterlevelLineModel.setTitle("Verlauf Wasserstand");
        waterlevelLineModel.setLegendPosition("e");
        waterlevelLineModel.setShowPointLabels(true);
        waterlevelLineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = waterlevelLineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);
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

    public void createUser() {
        System.out.println("Erschaffe neuen User :D");
        ArduinoUser arduinoUser = new ArduinoUser();
        arduinoUser.setFirstname("Jannik");
        arduinoUser.setLastname("Bergmann");
        arduinoUser.setUsername("admin");
        arduinoUser.setPassword("admin");

        arduinoUserRepo.newArduinoUser(arduinoUser);
    }

    public void createRandomData() {

        SensorData sensorData = new SensorData(
                getRandomIntegerBetweenRange(0, 100),
                getRandomIntegerBetweenRange(0, 100),
                getRandomIntegerBetweenRange(0, 100),
                getRandomIntegerBetweenRange(0, 100),
                getRandomIntegerBetweenRange(0, 100)
        );
        sensorDataRepo.newSensorData(sensorData);
        currentSensorData = sensorData;
        sensorDataCollection.add(sensorData);
        System.out.println("Neue SensorData: " + sensorData);
        this.waterlevelLineModel = ChartUtil.drawWaterlevelChart(sensorDataCollection);
        this.soilmoistureLineModel = ChartUtil.drawSoilMoistureChart(sensorDataCollection);
        this.airhumidityLineModel = ChartUtil.drawAirhumidityChart(sensorDataCollection);
    }

    public String goToIndex() {
        return "index";
    }

    public void showPageWaterlevel() {
        this.page = "waterlevel";
        this.waterlevelLineModel = ChartUtil.drawWaterlevelChart(sensorDataCollection);
    }

    public void showPageAirhumidity() {
        this.page = "airhumidity";
        this.airhumidityLineModel = ChartUtil.drawAirhumidityChart(sensorDataCollection);
    }

    public void showPageLightintensity() {
        this.page = "lightintensity";
        lightintensityLineModel = ChartUtil.drawLightintensityChart(sensorDataCollection);
    }

    public void showPageSoilmoisture() {
        this.page = "soilmoisture";
        this.soilmoistureLineModel = ChartUtil.drawSoilMoistureChart(sensorDataCollection);
    }

    public void showPageTemperature() {
        this.page = "temperature";
        temperatureLineModel = ChartUtil.drawTemperatureChart(sensorDataCollection);
    }

    public void showPageWiki() {
        this.page = "wiki";
        System.out.println("de.hsos.kbse.boundary.ArduinoBoundary.showPageWiki()");
    }
}
