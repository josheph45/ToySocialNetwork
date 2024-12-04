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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsController implements Observer<FriendshipEvent> {
    private Runnable onMessage;
    private Service service;
    private final ObservableList<Friendship> friendsList = FXCollections.observableArrayList();

    @FXML
    private TextField filterUsernameField;

    @FXML
    private TableView<Friendship> friendsTableView;

    @FXML
    private TableColumn<Friendship, String> friendUsernameColumn;

    @FXML
    private TableColumn<Friendship, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Friendship, Void> messageColumn;

    @FXML
    private TableColumn<Friendship, Void> deleteColumn;

    /**
     * Set the service and add this controller as an observer for friendship events.
     * Load the friendships of the current user.
     * @param service the service to be set
     */
    public void setService(Service service) {
        this.service = service;
        this.service.addFriendshipObserver(this);
        loadFriendships();
    }

    /**
     * Set the onMessage runnable
     * @param onMessage the runnable to be set
     */
    public void setOnMessage(Runnable onMessage) {
        this.onMessage = onMessage;
    }

    /**
     * Initialize the columns of the table view.
     * Add a listener to the filter text field to filter the friendships by username.
     * Add a delete button to the table view.
     */
    @FXML
    public void initialize() {
        friendUsernameColumn.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long currentUserId = service.getCurrentUserId();
            Long friendId = friendship.getUser1Id().equals(currentUserId)
                    ? friendship.getUser2Id()
                    : friendship.getUser1Id();

            String friendUsername = service.getUserById(friendId)
                    .map(User::getUsername)
                    .orElse("Unknown");

            return new SimpleStringProperty(friendUsername);
        });

        dateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFriendsFrom())
        );

        addMessageButtonToTable();
        addDeleteButtonToTable();

        filterUsernameField.textProperty().addListener((observable, oldValue, newValue) -> filterFriendships());
    }

    /**
     * Load the friendships of the current user and set them to the table view.
     */
    private void loadFriendships() {
        Iterable<Friendship> friendships = service.getFriendshipsOfUser(service.getCurrentUserId());
        friendsList.setAll((List<Friendship>) friendships);
        friendsTableView.setItems(friendsList);
    }

    /**
     * Handle the deletion of a friendship.
     * Delete the friendship from the database and reload the friendships.
     * @param friendship the friendship to be deleted
     */
    private void handleDeleteFriend(Friendship friendship) {
        service.deleteFriendship(friendship.getId());
        loadFriendships();
    }

    /**
     * Filter the friendships by the username entered in the filter text field.
     * The friendships are filtered by the username of the friend.
     */
    private void filterFriendships() {
        loadFriendships();

        String username = filterUsernameField.getText().toLowerCase();
        Long currentUserId = service.getCurrentUserId();

        List<Friendship> filteredFriendships = friendsList.stream()
                .filter(friendship -> {
                    Long friendId = friendship.getUser1Id().equals(currentUserId)
                            ? friendship.getUser2Id()
                            : friendship.getUser1Id();

                    return service.getUserById(friendId)
                            .map(user -> user.getUsername().toLowerCase().contains(username))
                            .orElse(false); // Exclude friendships where the user is not found
                })
                .collect(Collectors.toList());

        friendsList.setAll(filteredFriendships);
    }

    /**
     * Add a message button to the table view.
     */
    @FXML
    public void addMessageButtonToTable() {
        messageColumn.setCellFactory(param -> new TableCell<Friendship, Void>() {
            private final Button messageButton = new Button("Message");
            {
                messageButton.setOnAction(event -> {
                    Friendship friendship = getTableView().getItems().get(getIndex());
                    Long currentUserId = service.getCurrentUserId();
                    Long friendId = friendship.getUser1Id().equals(currentUserId)
                            ? friendship.getUser2Id()
                            : friendship.getUser1Id();
                    service.setSelectedUserId(friendId);
                    if (onMessage != null) {
                        onMessage.run();
                    } else {
                        System.err.println("Error: onMessage is not set!");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(messageButton);
                }
            }
        });
    }

    /**
     * Add a delete button to the table view.
     * The button is added to the last column of the table view.
     */
    @FXML
    public void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<Friendship, Void>() {
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

    /**
     * Update the friendships when a friendship event is received.
     * @param friendshipEvent the friendship event to be handled
     */
    @Override
    public void update(FriendshipEvent friendshipEvent) {
        if (friendshipEvent.getType() == EventEnum.RELOAD) {
            loadFriendships();
        }
    }
}