/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary;

import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.util.ChartUtil;
import de.hsos.kbse.entities.User;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.SensordataRepository;
import de.hsos.kbse.util.SessionUtils;
import de.hsos.kbse.repos.UserRepository;
import de.hsos.kbse.server.App;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.RollbackException;
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
 * <p>
 * Boundary Class for main app.
 * </p>
 *
 * @author Jannik Bergmann
 */
@ViewScoped
@Named(value = "arduino")
@Getter
@Setter
public class ArduinoBoundary implements Serializable {

    @Inject
    SensordataRepository sensorDataRepo;
    @Inject
    ArduinoRepository arduinoRepo;
    @Inject
    UserRepository arduinoUserRepo;

    @Inject
    App app;

    private List<Arduino> arduinos;
    User currentUser;
    private Sensordata currentSensorData;
    private Arduino currentArduino;

    private String page;

    private PieChartModel waterlevel;
    private PieChartModel soilhumidity;

    private LineChartModel waterlevelLineModel;
    private LineChartModel soilmoistureLineModel;
    private LineChartModel airhumidityLineModel;
    private LineChartModel lightintensityLineModel;
    private LineChartModel temperatureLineModel;
    private LineChartModel fertilizerlevelLineModel;

    private BarChartModel barModel;

    List<Sensordata> sensorDataCollection;

    private boolean waterPumpIsOn;
    private boolean fertilizerPumpIsOn;

    /**
     * <p>
     * Sets default configuration of Class and loads data to logged in User
     * </p>
     */
    @PostConstruct
    public void init() {

        waterPumpIsOn = false;
        fertilizerPumpIsOn = false;
        page = "landing";

        sensorDataCollection = new ArrayList();

        currentUser = arduinoUserRepo.getUser(SessionUtils.getUserId());
        arduinos = new ArrayList<>();
        if(currentUser != null) {
            arduinos = arduinoRepo.getAllArduinosByUser(currentUser);
            currentArduino = arduinos.get(0);
        }
        updateData();
    }

    /**
     * <p>
     * Draws charts from newest Sensordata.
     * </p>
     *
     * @param sensordataList
     */
    private void drawModels(List<Sensordata> sensordataList) {

        this.waterlevelLineModel = ChartUtil.drawWaterlevelChart(sensordataList);
        this.soilmoistureLineModel = ChartUtil.drawSoilMoistureChart(sensordataList);
        this.airhumidityLineModel = ChartUtil.drawAirhumidityChart(sensordataList);
        this.temperatureLineModel = ChartUtil.drawTemperatureChart(sensordataList);
        this.lightintensityLineModel = ChartUtil.drawLightintensityChart(sensordataList);
        this.fertilizerlevelLineModel = ChartUtil.drawFertilizerlevelChart(sensordataList);
        this.barModel = ChartUtil.drawBarModel(currentSensorData);
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageWaterlevel() {
        this.page = "waterlevel";

    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageAirhumidity() {
        this.page = "airhumidity";
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageLightintensity() {
        this.page = "lightintensity";
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageSoilmoisture() {
        this.page = "soilmoisture";
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageTemperature() {
        this.page = "temperature";
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageFertilizerlevel() {
        this.page = "fertilizerlevel";
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageWiki() {
        this.page = "wiki";
        System.out.println("de.hsos.kbse.boundary.ArduinoBoundary.showPageWiki()");
    }

    /**
     * <p>
     * Navigation
     * </p>
     */
    public void showPageAccount() {
        this.page = "account";
    }

    /**
     * <p>
     * Gets Last 100 Sensordata-Entries from Database and draws Chars again with
     * new Data.
     * </p>
     */
    public void updateData() {

        sensorDataCollection = sensorDataRepo.getLast100EntriesByArduino(arduinos.get(0));

        this.currentSensorData = sensorDataCollection.get(sensorDataCollection.size() - 1);

        List<Sensordata> sensordataList = sensorDataCollection;
        sensordataList = IntStream.range(0, sensordataList.size())
                .filter(n -> n % 4 == 0)
                .mapToObj(sensordataList::get)
                .collect(Collectors.toList());

        drawModels(sensordataList);
    }

    /**
     * <p>
     * Deletes User-Account
     * </p>
     */
    public void accountLoeschen() {
        arduinoUserRepo.deleteUser(currentUser);
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            FacesContext.getCurrentInstance().addMessage(
                    "loginform:password",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Dein Account wurde gelöscht.",
                            "Du kannst dich jederzeit wieder registrieren."));
            ec.getFlash().setKeepMessages(true);
        try {
            ec.redirect("/GardenDock/login.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ArduinoBoundary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>
     * Changes setWaterlevel in Arduino
     * </p>
     */
    public void changeWaterlevel() {
        try {
            currentArduino = arduinoRepo.updateArduino(currentArduino);
        } catch (RollbackException ex) {
            Logger.getLogger(ArduinoBoundary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>
     * Changes setFertilizerlevel in Arduino
     * </p>
     */
    public void changeFertilizerLevel() {
        try {
            currentArduino = arduinoRepo.updateArduino(currentArduino);
        } catch (RollbackException ex) {
            Logger.getLogger(ArduinoBoundary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>
     * Toggles waterpump of Arduino
     * </p>
     */
    public void toggleWaterPump() {
        if (waterPumpIsOn) {
            waterPumpIsOn = false;
            app.waterPumpOff(currentArduino.getArduinoId());
        } else {
            waterPumpIsOn = true;
            app.waterPumpOn(currentArduino.getArduinoId());
        }

    }

    /**
     * <p>
     * Toggles fertilizerpump of Arduino
     * </p>
     */
    public void toggleFertilizerPump() {
        if (fertilizerPumpIsOn) {
            fertilizerPumpIsOn = false;
            app.fertilizerPumpOff(currentArduino.getArduinoId());
        } else {
            fertilizerPumpIsOn = true;
            app.fertilizerPumpOn(currentArduino.getArduinoId());
        }

    }

}
