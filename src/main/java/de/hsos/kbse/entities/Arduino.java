/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @OneToMany(mappedBy = "arduino", cascade = CascadeType.REMOVE)
    private Set<Sensordata> sensorDatas = new HashSet<>();

    @NotNull
    @ManyToOne
    private User user;
    
    @Min(value = 0) @Max(value = 100)
    private int setWaterLevel;
    @Min(value = 0) @Max(value = 60)
    private int fertilizerIntervallInDays;
    
    // Timestamp
    @NotNull
    @ToString.Include
    private LocalDateTime lastFertilization;
    
    public Arduino() {
        this.comPort = "";
        this.name = "";
        this.fertilizerIntervallInDays = 0;
        this.lastFertilization = LocalDateTime.now();
    }
}
