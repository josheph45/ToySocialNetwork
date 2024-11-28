package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.FriendshipEvent;
import app.toysocialnetwork.utils.observer.Observer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsController implements Observer<FriendshipEvent> {
    private Service service;
    private ObservableList<Friendship> friendsList = FXCollections.observableArrayList();

    // The TextFields used for filtering the friendships
    @FXML
    private TextField filterUsernameField;

    // The TableView to display the friendships
    @FXML
    private TableView<Friendship> friendsTableView;

    // The columns in the TableView
    @FXML
    private TableColumn<Friendship, String> friendUsernameColumn;

    @FXML
    private TableColumn<Friendship, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Friendship, Void> deleteColumn;

    // Method to set up the service
    public void setService(Service service) {
        this.service = service;
        this.service.addFriendshipObserver(this); // Subscribe to friendship updates
        loadFriendships();  // Initial load of friendships
    }

//    @FXML
//    public void initialize() {
//        // Initialize TableView columns
//        friendUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("friendUsername"));
//        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
//        addDeleteButtonToTable();
//
//        // Add listener to filterUsernameField
//        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> filterFriendships());
//    }


    @FXML
    public void initialize() {
        // Friend Username Column
        friendUsernameColumn.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long currentUserId = service.getCurrentUserId();
            Long friendId = friendship.getUser1Id().equals(currentUserId)
                    ? friendship.getUser2Id()
                    : friendship.getUser1Id();

            // Resolve the friend's username dynamically
            String friendUsername = service.getUserById(friendId)
                    .map(User::getUsername)
                    .orElse("Unknown");

            return new SimpleStringProperty(friendUsername);
        });

        // Date Column
        dateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFriendsFrom())
        );

        // Add Delete Button
        addDeleteButtonToTable();

        // Filter Friends
        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> filterFriendships());
    }


    private void loadFriendships() {
        Iterable<Friendship> friendships = service.getFriendshipsOfUser(service.getCurrentUserId());
        friendsList.setAll((List<Friendship>) friendships);
        friendsTableView.setItems(friendsList);
    }

    private void handleDeleteFriend(Friendship friendship) {
        service.deleteFriendship(friendship.getId());
        loadFriendships();
    }

    private void filterFriendships() {
        String username = filterUsernameField.getText().toLowerCase();

        // Load all friendships
        loadFriendships();

        Long currentUserId = service.getCurrentUserId();

        // Filter by username of the user that is not the current user
        List<Friendship> filteredFriendships = friendsList.stream()
                .filter(friendship -> {
                    // Determine the friend's ID
                    Long friendId = friendship.getUser1Id().equals(currentUserId)
                            ? friendship.getUser2Id()
                            : friendship.getUser1Id();

                    // Get the friend's username
                    return service.getUserById(friendId)
                            .map(user -> user.getUsername().toLowerCase().contains(username))
                            .orElse(false); // Exclude friendships where the user is not found
                })
                .collect(Collectors.toList());

        friendsList.setAll(filteredFriendships);
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Friendship friendship = getTableView().getItems().get(getIndex());
                    handleDeleteFriend(friendship);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    @Override
    public void update(FriendshipEvent friendshipEvent) {
        // Update the user list based on the event (e.g., when a user is added, removed, etc.)
        if (friendshipEvent.getType() == EventEnum.RELOAD) {
            loadFriendships();  // Reload the list if the event is a reload
        }
    }
}