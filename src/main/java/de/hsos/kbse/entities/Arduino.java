/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import java.net.InetAddress;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */
@Entity
@Access(AccessType.FIELD)
@Data
public class Arduino implements Serializable {
    @Id
    long id;
    @OneToOne
    @Inject
    SensorData sensorData;
    InetAddress ipAddress;
}
