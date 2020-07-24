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
    
    @ManyToOne
    private Arduino arduino;
    
    // Sensor datas
    int waterMeter;
    int dungMeter;
    int sunLevel;
    int airHumidity;
    int soilHumidity;
    int temperature;
    
    // Timestamp
    @Temporal(javax.persistence.TemporalType.DATE) // wird das gebraucht?
    Date timestamp;

    @Override
    public String toString() {
        return "ajskdjka";
    }
    
}
