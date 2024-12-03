package app.toysocialnetwork.controller;

import app.toysocialnetwork.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {
    private Runnable onLogOut;
    private Runnable onUsers;
    private Runnable onFriends;
    private Runnable onRequests;
    private Runnable onPendings;
    private Service service;

    @FXML
    private Label loggedInAsLabel;

    @FXML
    private Button usersButton;

    @FXML
    private Button friendsButton;

    @FXML
    private Button requestsButton;

    @FXML
    private Button pendingsButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button deleteAccountButton;

    /**
     * Set the service for the controller.
     * @param service the service to be set
     */
    public void setService(Service service) {
        this.service = service;
        updateView();
    }

    /**
     * Set the onLogOut runnable.
     * @param onLogOut the runnable to be set
     */
    public void setOnLogOut(Runnable onLogOut) {
        this.onLogOut = onLogOut;
    }

    /**
     * Set the onUsers runnable.
     * @param onUsers the runnable to be set
     */
    public void setOnUsers(Runnable onUsers) {
        this.onUsers = onUsers;
    }

    /**
     * Set the onFriends runnable.
     * @param onFriends the runnable to be set
     */
    public void setOnFriends(Runnable onFriends) {
        this.onFriends = onFriends;
    }

    /**
     * Set the onRequests runnable.
     * @param onRequests the runnable to be set
     */
    public void setOnRequests(Runnable onRequests) {
        this.onRequests = onRequests;
    }

    /**
     * Set the onPendings runnable.
     * @param onPendings the runnable to be set
     */
    public void setOnPendings(Runnable onPendings) {
        this.onPendings = onPendings;
    }

    /**
     * Initialize the controller.
     * Add listeners to the buttons.
     */
    @FXML
    private void initialize() {
        logOutButton.setOnAction(event -> handleLogOut());
        deleteAccountButton.setOnAction(event -> handleDeleteAccount());
        usersButton.setOnAction(event -> onUsers.run());
        friendsButton.setOnAction(event -> onFriends.run());
        requestsButton.setOnAction(event -> onRequests.run());
        pendingsButton.setOnAction(event -> onPendings.run());
    }

    /**
     * Handle the log out button click.
     */
    private void handleLogOut() {
        System.out.println("Log Out button clicked");
        service.setCurrentUserId(null);
        onLogOut.run();
    }

    /**
     * Handle the delete account button click.
     */
    private void handleDeleteAccount() {
        System.out.println("Delete Account button clicked");
        service.deleteUser(service.getCurrentUserId());
        onLogOut.run();
    }

    /**
     * Update the view.
     */
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