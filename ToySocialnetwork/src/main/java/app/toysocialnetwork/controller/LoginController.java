package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class LoginController {
    private Runnable onLogin;
    private Service service;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    /**
     * Set the service for the controller
     * @param service the service to be set
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Set the onLogin runnable
     * @param onLogin the runnable to be set
     */
    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }

    /**
     * Initialize the controller
     * Add a listener to the login button
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        Optional<User> user = service.findUserByUsername(username);
        if(user.isPresent()) {
            if(user.get().getPassword().equals(password)) {
                showAlert("Login Successful", "Welcome " + username + "!", Alert.AlertType.INFORMATION);
                service.setCurrentUserId(user.get().getId());

                service.notifyFriendshipObservers(new FriendshipEvent(EventEnum.RELOAD, null));
                service.notifyRequestObservers(new RequestEvent(EventEnum.RELOAD, null));
                service.notifyUserObservers(new UserEvent(EventEnum.RELOAD, null));

                System.out.println("Login with user Id: " + service.getCurrentUserId());
                if(onLogin != null) {
                    onLogin.run();
                }

            } else {
                showAlert("Login Failed", "Incorrect password!", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Login Failed", "User not found!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show an alert with the given title, content and type
     * @param title the title of the alert
     * @param content the content of the alert
     * @param type the type of the alert
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
