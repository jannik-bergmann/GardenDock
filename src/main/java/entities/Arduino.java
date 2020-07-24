/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

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
    
    private String comPort;
    private String name;
    
    @OneToMany(mappedBy = "arduino", cascade = CascadeType.REMOVE)
    private Set<Sensordata> sensorDatas = new HashSet<>();
    
    @ManyToOne
    private User user;
}
