/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.entities.authorization;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Hold username and Password for Authentification
 * @author Jannik Bergmann 
 */
@Named
@RequestScoped
public class Credentials {
    
    @Size(min = 3, max = 15,message = "Nutzername muss min. 3 und max. 15 Buchstaben enthalten.")
    private String username;

    @Size(min = 3, max = 15,message = "Passwort muss min. 3 und max. 15 Buchstaben enthalten.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
