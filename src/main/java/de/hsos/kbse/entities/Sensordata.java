/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import de.hsos.kbse.entities.Arduino;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author bastianluhrspullmann
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Sensordata implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String sensorId;

    @NotNull
    @ManyToOne
    private Arduino arduino;

    // Sensor datas
    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    int waterlevel;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    int fertilizerlevel;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    int lightintensity;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    int airhumidity;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    int soilhumidity;

    @Min(value = -20)
    @Max(value = 40)
    @ToString.Include
    double temperature;

    // Timestamp
    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE) // wird das gebraucht?
    @ToString.Include
    LocalDateTime timeOfCapture;

    public Sensordata(int waterlevel, int airhumidity, int soilhumidity, int lightintensity, int fertilizerlevel, double temperature) {
        this.waterlevel = waterlevel;
        this.airhumidity = airhumidity;
        this.soilhumidity = soilhumidity;
        this.lightintensity = lightintensity;
        this.temperature = temperature;
        this.fertilizerlevel = fertilizerlevel;
        this.timeOfCapture = LocalDateTime.now();
    }

    public Sensordata() {
        this.waterlevel = 0;
        this.airhumidity = 0;
        this.soilhumidity = 0;
        this.lightintensity = 0;
        this.temperature = 0;
        this.timeOfCapture = LocalDateTime.now();
    }
}
