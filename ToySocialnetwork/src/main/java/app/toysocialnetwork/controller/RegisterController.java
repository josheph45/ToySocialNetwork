package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class RegisterController {
    private Service service;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    public void setService(Service service) {
        this.service = service; // Inject the Service
    }

    @FXML
    public void handleRegister() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if any fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        // Check if the username already exists using the Service class
        if (service.findUserByUsername(username).isPresent()) {
            showAlert("Registration Failed", "Username already exists!", Alert.AlertType.ERROR);
            return;
        }

        User newUser = new User(firstName, lastName, username, password);
        Optional<User> user = service.addUser(newUser);

        if (user.isPresent()) {
            showAlert("Registration Successful", "Proceed to login", Alert.AlertType.INFORMATION);
            clearFields();
        } else {
            showAlert("Registration Failed", "An error occurred!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Clear the input fields after successful registration
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
    }
}
