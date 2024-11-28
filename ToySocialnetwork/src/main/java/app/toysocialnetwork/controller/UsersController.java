package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.UserEvent;
import app.toysocialnetwork.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UsersController implements Observer<UserEvent> {
    private Service service;
    private ObservableList<User> usersList = FXCollections.observableArrayList();

    // The TextFields used for filtering the users
    @FXML
    private TextField filterUsernameField;

    @FXML
    private TextField filterFirstNameField;

    @FXML
    private TextField filterLastNameField;

    // The TableView to display the users
    @FXML
    private TableView<User> usersTableView;

    // The columns in the TableView
    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    // Method to set up the service
    public void setService(Service service) {
        this.service = service;
        this.service.addUserObserver(this); // Subscribe to user updates
        loadUsers();  // Initial load of users
    }

    @FXML
    public void initialize() {
        // Initialize TableView columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Add listeners to filter fields for live filtering
        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());
        filterFirstNameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());
        filterLastNameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());
    }

    // Method to load the users from the service
    private void loadUsers() {
        Iterable<User> users = service.getUsers();
        usersList.setAll((List<User>) users);
        usersTableView.setItems(usersList);
    }

    // Method to filter users based on the values in the filter fields
    private void filterUsers() {
        String usernameFilter = filterUsernameField.getText().toLowerCase();
        String firstNameFilter = filterFirstNameField.getText().toLowerCase();
        String lastNameFilter = filterLastNameField.getText().toLowerCase();

        // Load users for every filter change
        loadUsers();

        // Otherwise, filter based on the input in the fields
        List<User> filteredUsers = usersList.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(usernameFilter))
                .filter(user -> user.getFirstName().toLowerCase().contains(firstNameFilter))
                .filter(user -> user.getLastName().toLowerCase().contains(lastNameFilter))
                .collect(Collectors.toList());

        usersList.setAll(filteredUsers);  // Update the table view with the filtered list
    }

    @Override
    public void update(UserEvent userEvent) {
        // Update the user list based on the event (e.g., when a user is added, removed, etc.)
        if (userEvent.getType() == EventEnum.RELOAD) {
            loadUsers();  // Reload the list if the event is a reload
        }
    }
}
