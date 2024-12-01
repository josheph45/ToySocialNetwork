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
    private Service service;
    private final ObservableList<Friendship> friendsList = FXCollections.observableArrayList();

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

    public void setService(Service service) {
        this.service = service;
        this.service.addFriendshipObserver(this);
        loadFriendships();
    }

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

        addDeleteButtonToTable();

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

    private void addDeleteButtonToTable() {
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

    @Override
    public void update(FriendshipEvent friendshipEvent) {
        if (friendshipEvent.getType() == EventEnum.RELOAD) {
            loadFriendships();
        }
    }
}