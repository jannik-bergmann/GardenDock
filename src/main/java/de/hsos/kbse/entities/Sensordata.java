/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import de.hsos.kbse.entities.Arduino;
import java.io.Serializable;
import java.time.Instant;
import javax.enterprise.inject.Vetoed;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author bastianluhrspullmann
 */
@Entity
@Data
public class Sensordata implements Serializable{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String sensorId;
    
    @NotNull
    @ManyToOne
    private Arduino arduino;
    
    // Sensor datas
    @Min(value = 0) @Max(value = 100)
    int waterlevel;
    @Min(value = 0) @Max(value = 100)
    int fertilizerlevel;
    @Min(value = 0) @Max(value = 100)
    int lightintensity;
    @Min(value = 0) @Max(value = 100)
    int airhumidity;
    @Min(value = 0) @Max(value = 100)
    int soilhumidity;
    @Min(value = -20) @Max(value = 40)
    double temperature;
    
    // Timestamp
    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE) // wird das gebraucht?
    Date timeOfCapture;

    public Sensordata(int waterlevel, int airhumidity, int soilhumidity,int lightintensity,int fertilizerlevel, double temperature){
        this.waterlevel = waterlevel;
        this.airhumidity = airhumidity;
        this.soilhumidity = soilhumidity;
        this.lightintensity = lightintensity;
        this.temperature = temperature;
        this.fertilizerlevel = fertilizerlevel;
        this.timeOfCapture = new Date();
    }
    
    
    public Sensordata(){
        this.waterlevel = 0;
        this.airhumidity = 0;
        this.soilhumidity = 0;
        this.lightintensity = 0;
        this.temperature = 0;
        this.timeOfCapture = new Date();
    }
}
