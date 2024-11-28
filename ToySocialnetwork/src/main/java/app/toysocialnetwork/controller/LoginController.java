package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;

import app.toysocialnetwork.utils.event.FriendshipEvent;
import app.toysocialnetwork.utils.event.RequestEvent;
import app.toysocialnetwork.utils.event.UserEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    public void setService(Service service) {
        this.service = service;
    }

    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if any fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        // Find the user by username
        Optional<User> user = service.findUserByUsername(username);
        if(user.isPresent()) {
            // Check if the password is correct
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

    // Add a method to show an alert
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
