/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary.authorization;

//import de.hsos.kbse.controller.ArduinoUserRepoImpl;
import de.hsos.kbse.repos.UserRepository;
import de.hsos.kbse.entities.authorization.Credentials;
//import de.hsos.kbse.entities.interfaces.ArduinoUserRepo;
import de.hsos.kbse.util.SessionUtils;
import de.hsos.kbse.entities.User;
import de.hsos.kbse.repos.ArduinoRepository;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Login implements Serializable {

    @Inject
    Credentials credentials;

    @Inject
    Credentials registerCredentials;
    
    @Inject
    UserRepository arduinoUserRepo;
    
    @Inject
    ArduinoRepository arduinoRepo;
    
    private boolean showRegisterIcon;

    private User user;
    private String page;
    
    @PostConstruct
    private void init(){
        page = "loginForm";
        showRegisterIcon = true;
    }

    public String login() {

        boolean loginValid = validateUser(credentials);
        if (loginValid) {
            HttpSession session = SessionUtils.getSession();
            //session.setMaxInactiveInterval(10);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getUserId());
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
        //TODO: Hole User aus DB
        //List<ArduinoUser> results = arduinoUserRepo.getArduinoUserByCredentials(credentials);
        
        List<User> results=  arduinoUserRepo.getArduinoUserByCredentials(credentials);
        
        //List<User> results = new ArrayList();
        //results.add(createUser());
        
        if (!results.isEmpty()) {

            user = results.get(0);
            return true;
        } else {
            return false;
        }
    }
    
    public String register(){
        System.out.println("<----->Register");
        return "";
    }

    public void timeout() throws IOException {

        logout();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/GardenDock/login.xhtml");

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
    User getCurrentUser() {

        return user;

    }

    public User createUser() {
        System.out.println("Erschaffe neuen User :D");
        User arduinoUser = new User();
        arduinoUser.setUsername("admin");
        arduinoUser.setPwdhash("admin");
        

        //arduinoUserRepo.newArduinoUser(arduinoUser);
        return arduinoUser;
    }
    
    public void showLogin(){
        this.showRegisterIcon = true;
        this.page = "loginForm";
    }
    
    public void showRegister(){
        this.showRegisterIcon = false;
        this.page = "registerForm";
    }

}
