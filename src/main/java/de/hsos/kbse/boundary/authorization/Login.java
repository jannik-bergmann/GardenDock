/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.kbse.boundary.authorization;

//import de.hsos.kbse.controller.ArduinoUserRepoImpl;
import de.hsos.kbse.entities.Arduino;
import de.hsos.kbse.entities.Sensordata;
import de.hsos.kbse.repos.UserRepository;
import de.hsos.kbse.entities.authorization.Credentials;
//import de.hsos.kbse.entities.interfaces.ArduinoUserRepo;
import de.hsos.kbse.util.SessionUtils;
import de.hsos.kbse.entities.User;
import de.hsos.kbse.repos.ArduinoRepository;
import de.hsos.kbse.repos.SensordataRepository;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * <p>
 * Handles authentification process of Login-/RegistrationView
 * </p>
 * 
 *
 * @author Jannik Bergmann
 */
@ViewScoped
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
    SensordataRepository sensordataRepo;

    @Inject
    ArduinoRepository arduinoRepo;

    private boolean showRegisterIcon;

    private User user;
    private String page;

    /**
     * Sets default configuration of Login
     */
    @PostConstruct
    private void init() {
        page = "loginForm";
        showRegisterIcon = true;
    }

    /**
     * <p>
     * Validates user credentials and redirects to main app, if credentials are
     * valid. Also creates new HttpSession
     * </p>
     *
     * @return redirect-String to dashpgage or null
     */
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
                    "loginform:password",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Falscher Username oder Passwort",
                            "Bitte geben Sie gueltige Nutzerdaten ein"));
            return null;
        }

    }

    /**
     * <p>
     * Check if user exists in database
     * </p>
     *
     * @param credentials - username and password
     * @return true, if user exists and false,if user doesnt exist
     */
    private boolean validateUser(Credentials credentials) {

        List<User> results = arduinoUserRepo.getArduinoUserByCredentials(credentials);

        //List<User> results = new ArrayList();
        //results.add(createUser());
        if (!results.isEmpty()) {

            user = results.get(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Takes credentials of new User and checks if that username already exists.
     * If not it creates a new User. If it already existe, it returns
     * FacesMessage declaring this.
     * </p>
     */
    public void register() {
        List<User> results = arduinoUserRepo.getArduinoUserByCredentials(registerCredentials);

        if (results.isEmpty()) {
            user = new User();
            user.setUsername(registerCredentials.getUsername());
            user.setPwdhash(registerCredentials.getPassword());

            arduinoUserRepo.addUser(user);

            Arduino arduino = new Arduino();

            arduino.setName("arduino-" + user.getUsername());
            arduino.setComPort("dev/123");
            arduino.setFertilizerIntervallInDays(5);
            arduino.setLastFertilization(LocalDateTime.now());
            arduino.setSetWaterLevel(50);
            arduino.setUser(user);
            arduinoRepo.addArduino(arduino);

            Sensordata sensordata = new Sensordata(23, 12, 45, 63, 12, 19.5);
            sensordata.setArduino(arduino);
            sensordataRepo.addSensordata(sensordata);

            page = "loginForm";
            showRegisterIcon = true;
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            FacesContext.getCurrentInstance().addMessage(
                    "loginform:password",
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Account erfolgreich erstellt!",
                            "Du kannst dich jetzt einloggen."));
            ec.getFlash().setKeepMessages(true);
            try {
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    "registerform:username",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Username existiert schon!",
                            "Bitte gib einen anderen Namen ein."));

        }

    }
    /**
     * <p>
     * Invalidate session and redirects to LoginView
     * </p>
     * @return 
     */
    public String logout() {
        user = null;
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login?faces-redirect=true";
    }
    /**
     * <p>
     * Logs user out and redirects to login.xhtml
     * </p>
     * @throws IOException 
     */
    public void timeout() throws IOException {

        logout();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/GardenDock/login.xhtml");

    }

    /**
     * <p>
     * Inserts loginForm.xhtml
     * <p>
     * 
     */
    public void showLogin() {
        this.showRegisterIcon = true;
        this.page = "loginForm";
    }

    /**
     * <p>
     * Inserts registerForm.xhtml
     * <p>
     * 
     */
    public void showRegister() {
        this.showRegisterIcon = false;
        this.page = "registerForm";
    }

}
