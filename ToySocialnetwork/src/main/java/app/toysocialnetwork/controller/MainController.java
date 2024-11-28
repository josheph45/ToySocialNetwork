package app.toysocialnetwork.controller;

import app.toysocialnetwork.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {
    private Runnable onLogOut;
    private Service service;

    @FXML
    private Label loggedInAsLabel;

    @FXML
    private Button logOutButton;

    @FXML
    private Button deleteAccountButton;

    public void setService(Service service) {
        this.service = service;
        updateView();
    }

    public void setOnLogOut(Runnable onLogOut) {
        this.onLogOut = onLogOut;
    }

    @FXML
    private void initialize() {
        logOutButton.setOnAction(event -> handleLogOut());
        deleteAccountButton.setOnAction(event -> handleDeleteAccount());
    }

    private void handleLogOut() {
        // Perform log out logic
        System.out.println("Log Out button clicked");
        // Call service method to log out user
        service.setCurrentUserId(null);
        // Call onLogOut callback
        onLogOut.run();
    }

    private void handleDeleteAccount() {
        // Perform delete account logic
        System.out.println("Delete Account button clicked");
        // Call service method to delete account and log out user
        service.deleteUser(service.getCurrentUserId());
        // Redirect to the login view or exit application
        onLogOut.run();
    }

    private void updateView() {
        if (service != null) {
            service.getUserById(service.getCurrentUserId())
                    .ifPresentOrElse(
                            user -> loggedInAsLabel.setText("Logged in as: " + user.getUsername()),
                            () -> loggedInAsLabel.setText("User not found")
                    );
        }
    }
}