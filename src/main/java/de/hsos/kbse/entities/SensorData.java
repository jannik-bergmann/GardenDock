/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */

@Data
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor
@SequenceGenerator(name = "id_gen", sequenceName = "id_gen",  initialValue = 2)

public class SensorData implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "id_gen")
    private long id;
    
    int waterlevel;
    int airhumidity;
    int soilhumidity;
    int lightintensity;
    double temperature;
    
    public SensorData(int waterlevel, int airhumidity, int soilhumidity,int lightintensity, double temperature){
        this.waterlevel = waterlevel;
        this.airhumidity = airhumidity;
        this.soilhumidity = soilhumidity;
        this.lightintensity = lightintensity;
        this.temperature = temperature;
    }
    
    public int getWaterlevelInPercent(){
        return waterlevel;
    }
    
    public int getAirhumidityInPercent(){
        return airhumidity;
    }
    
    public int getSoilhumidityInPercent(){
        return soilhumidity;
    }
    
    public int getLightIntensityInPercent(){
        return lightintensity;
    }
    
    public double getTemperatureInDegreesCelsius(){
        return temperature;
    }
}
