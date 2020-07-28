/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.enterprise.context.Dependent;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/** Sensordata Entity
 *
<<<<<<< HEAD
 * @author Bastian Lührs-Püllmann
=======
 * @author bastianluhrspullmann & Jannik Bergmann
>>>>>>> d5b342fc64ced32b8087012d8fe20d3e2a43f3d6
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
    private int waterlevel;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    private int fertilizerlevel;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    private int lightintensity;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    private int airhumidity;

    @Min(value = 0)
    @Max(value = 100)
    @ToString.Include
    private int soilhumidity;

    @Min(value = -20)
    @Max(value = 40)
    @ToString.Include
    private double temperature;

    // Timestamp
    @NotNull
    @ToString.Include
    private LocalDateTime timeOfCapture;

    
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
        this.fertilizerlevel = 0;
        this.airhumidity = 0;
        this.soilhumidity = 0;
        this.lightintensity = 0;
        this.temperature = 0;
        this.timeOfCapture = LocalDateTime.now();
    }
}
