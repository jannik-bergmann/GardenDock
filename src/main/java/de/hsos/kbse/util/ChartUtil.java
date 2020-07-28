/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.util;

import de.hsos.kbse.entities.Sensordata;
import java.util.List;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author Jannik Bergmann 
 */
public class ChartUtil {

    public static LineChartModel drawWaterlevelChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("waterlevel");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getWaterlevel());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Wasserstand");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }

    public static LineChartModel drawSoilMoistureChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("Bodenfeuchtigkeit");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getSoilhumidity());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Bodenfeuchtigkeit");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }

    public static LineChartModel drawAirhumidityChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("Luftfeuchtigkeit");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getAirhumidity());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Luftfeuchtigkeit");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }

    public static LineChartModel drawLightintensityChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("Lichtintensitaet");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getLightintensity());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Lichtintensitaet");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Prozent");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }

    public static LineChartModel drawTemperatureChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("Temperatur");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getTemperature());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Temperatur");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Grad");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }
    
    public static LineChartModel drawFertilizerlevelChart(List<Sensordata> sensorDataCollection) {

        LineChartModel lineModel = new LineChartModel();

        ChartSeries waterlevelSeries = new ChartSeries();
        waterlevelSeries.setLabel("Duenger");
        sensorDataCollection.forEach((sensorData) -> {
            waterlevelSeries.set(sensorData.getTimeOfCapture().getMinute() + ":" + sensorData.getTimeOfCapture().getSecond(),
                    sensorData.getFertilizerlevel());
        });

        lineModel.addSeries(waterlevelSeries);

        lineModel.setTitle("Verlauf Duenger");
        lineModel.setLegendPosition("e");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Zeit"));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("Grad");
        yAxis.setMin(0);
        yAxis.setMax(100);

        return lineModel;
    }

}
