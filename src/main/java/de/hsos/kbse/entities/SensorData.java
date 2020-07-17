/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 */

@Data
@Entity
@Access(AccessType.FIELD)
public class SensorData implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    int waterlevel;
    int airhumidity;
    int soilhumidity;
    int lightintensity;
}
