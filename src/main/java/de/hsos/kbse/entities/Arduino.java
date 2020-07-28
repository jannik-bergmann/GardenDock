/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import de.hsos.kbse.iotGateway.IotGatewaySimulator;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Arduino Entity
 *
 * @author Bastian Luehrs-Puellmann,  Jannik Bergmann
*/ 

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(IotGatewaySimulator.class)
public class Arduino implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String arduinoId;

    @NotNull
    @ToString.Include
    private String comPort;
    @ToString.Include
    private String name;

    @OneToMany(orphanRemoval = true, mappedBy = "arduino")
    private Set<Sensordata> sensorDatas = new HashSet<>();
    
    @NotNull
    @ManyToOne
    private User user;

    @Min(value = 0) @Max(value = 100)
    private int setWaterLevel;
    @Min(value = 0) @Max(value = 60)
    private int fertilizerIntervallInDays;
    
    // Timestamp
    @ToString.Include
    @NotNull
    private LocalDateTime lastFertilization;
    
    public Arduino() {
        this.setWaterLevel = 0;
        this.comPort = "";
        this.name = "";
        this.fertilizerIntervallInDays = 0;
        this.lastFertilization = LocalDateTime.now();
    }
    
}
