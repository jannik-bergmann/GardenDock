/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entities.Arduino;
import java.io.Serializable;
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
    int waterMeter;
    @Min(value = 0) @Max(value = 100)
    int dungMeter;
    @Min(value = 0) @Max(value = 100)
    int sunLevel;
    @Min(value = 0) @Max(value = 100)
    int airHumidity;
    @Min(value = 0) @Max(value = 100)
    int soilHumidity;
    @Min(value = -20) @Max(value = 40)
    int temperature;
    
    // Timestamp
    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE) // wird das gebraucht?
    Date timestamp;

    @Override
    public String toString() {
        return "ajskdjka";
    }
    
}
