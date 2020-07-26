/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import de.hsos.kbse.entities.User;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Arduino implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String arduinoId;
    
    @NotNull
    private String comPort;
    private String name;
    
    @OneToMany(mappedBy = "arduino", cascade = CascadeType.REMOVE)
    private Set<Sensordata> sensorDatas = new HashSet<>();
    
    @NotNull
    @ManyToOne
    private User user;
    
    @Min(value = 0) @Max(value = 100)
    private int setWaterLevel;
    @Min(value = 0) @Max(value = 100)
    private int currentWaterLevel;
    @Min(value = 0) @Max(value = 100)
    private int setFertilizerLevel;
    @Min(value = 0) @Max(value = 100)
    private int currentFertilizerLevel;
}
