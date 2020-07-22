/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary.authorization;

import de.hsos.kbse.controller.ArduinoUserRepoImpl;
import de.hsos.kbse.entities.authorization.Credentials;
import de.hsos.kbse.entities.ArduinoUser;
import de.hsos.kbse.entities.interfaces.ArduinoUserRepo;
import de.hsos.kbse.util.SessionUtils;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jannik Bergmann <jannik.bergmann@hs-osnabrueck.de>
 * Quelle:https://www.journaldev.com/7252/jsf-authentication-login-logout-database-example
 */
@SessionScoped
@Named
@Getter
@Setter
public class Login implements Serializable {

    @Inject
    Credentials credentials;

    @Inject
    ArduinoUserRepoImpl arduinoUserRepo;

    private ArduinoUser user;

    public String login() {

        boolean loginValid = validateUser(credentials);
        if (loginValid) {
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("username", user.getUsername());
            return "dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Falscher Username oder Passwort",
                            "Bitte geben Sie gültige Nutzerdaten ein"));
            return null;
        }

    }

    private boolean validateUser(Credentials credentials) {
        List<ArduinoUser> results = arduinoUserRepo.getArduinoUserByCredentials(credentials);
        if (!results.isEmpty()) {

            user = results.get(0);
            return true;
        } else {
            return false;
        }
    }

    public String logout() {
        user = null;
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login?faces-redirect=true";
    }

    public boolean isLoggedIn() {

        return user != null;

    }

    //@Produces
    //@LoggedIn
    ArduinoUser getCurrentUser() {

        return user;

    }

    public void createUser() {
        System.out.println("Erschaffe neuen User :D");
        ArduinoUser arduinoUser = new ArduinoUser();
        arduinoUser.setFirstname("Jannik");
        arduinoUser.setLastname("Bergmann");
        arduinoUser.setUsername("admin");
        arduinoUser.setPassword("admin");

        arduinoUserRepo.newArduinoUser(arduinoUser);
    }

}
