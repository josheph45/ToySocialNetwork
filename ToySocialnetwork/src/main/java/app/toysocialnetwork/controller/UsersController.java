package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.UserEvent;
import app.toysocialnetwork.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UsersController implements Observer<UserEvent> {
    private Runnable onViewProfile;
    private Service service;
    private final ObservableList<User> usersList = FXCollections.observableArrayList();

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

    @FXML
    private TableColumn<User, Void> viewProfileColumn;

    public void setService(Service service) {
        this.service = service;
        this.service.addUserObserver(this);
        loadUsers();
    }

    public void setOnViewProfile(Runnable onViewProfile) {
        this.onViewProfile = onViewProfile;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());
        filterFirstNameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());
        filterLastNameField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());

        addViewProfileButtonToTable();
    }

    private void loadUsers() {
        Iterable<User> users = service.getUsers();
        usersList.setAll((List<User>) users);
        usersTableView.setItems(usersList);
    }

    private void filterUsers() {
        loadUsers();

        String usernameFilter = filterUsernameField.getText().toLowerCase();
        String firstNameFilter = filterFirstNameField.getText().toLowerCase();
        String lastNameFilter = filterLastNameField.getText().toLowerCase();

        List<User> filteredUsers = usersList.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(usernameFilter))
                .filter(user -> user.getFirstName().toLowerCase().contains(firstNameFilter))
                .filter(user -> user.getLastName().toLowerCase().contains(lastNameFilter))
                .collect(Collectors.toList());

        usersList.setAll(filteredUsers);
    }

    @FXML
    public void addViewProfileButtonToTable() {
        viewProfileColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button viewButton = new Button("View");
            {
                viewButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    service.setSelectedUserId(selectedUser.getId());
                    if (onViewProfile != null) {
                        onViewProfile.run();
                    } else {
                        System.err.println("Error: onViewProfile is not set!");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    @Override
    public void update(UserEvent userEvent) {
        if (userEvent.getType() == EventEnum.RELOAD) {
            loadUsers();
        }
    }
}
