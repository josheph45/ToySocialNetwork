package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.Request;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.FriendshipEvent;
import app.toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfileController implements Observer<FriendshipEvent> {
    private Runnable onViewProfile;
    private Service service;
    private User viewedUser;
    private final ObservableList<User> friendsList = FXCollections.observableArrayList();

    // User detail labels
    @FXML
    private Label usernameLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    // Friends TableView and Columns
    @FXML
    private TableView<User> friendsTableView;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, Void> viewProfileColumn;

    // Action button (send/reject request or delete friend)
    @FXML
    private Button actionButton;

    public void setService(Service service, User viewedUser) {
        this.service = service;
        this.viewedUser = viewedUser;
        this.service.setSelectedUserId(viewedUser.getId());
        this.service.addFriendshipObserver(this);

        loadProfileDetails();
        loadFriends();
        configureActionButton();
    }

    public void setOnViewProfile(Runnable onViewProfile) {
        this.onViewProfile = onViewProfile;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        addViewProfileButtonToTable();
    }

    private void loadProfileDetails() {
        usernameLabel.setText("Username: " + viewedUser.getUsername());
        firstNameLabel.setText("First Name: " + viewedUser.getFirstName());
        lastNameLabel.setText("Last Name: " + viewedUser.getLastName());
    }

    private void loadFriends() {
        friendsList.clear();
        Iterable<Friendship> friendships = service.getFriendshipsOfUser(viewedUser.getId());
        Long currentUserId = viewedUser.getId();
        for (Friendship friendship : friendships) {
            Long friendId = friendship.getUser1Id().equals(currentUserId)
                    ? friendship.getUser2Id()
                    : friendship.getUser1Id();
            service.getUserById(friendId).ifPresent(friendsList::add);
        }
        friendsTableView.setItems(friendsList);
    }

    private void configureActionButton() {
        Long currentUserId = service.getCurrentUserId();
        Long viewedUserId = viewedUser.getId();

        if (currentUserId.equals(viewedUserId)) {
            actionButton.setVisible(false);
            return;
        }

        boolean isFriend = false;
        for (Friendship friendship : service.getFriendshipsOfUser(currentUserId)) {
            if ((friendship.getUser1Id().equals(viewedUserId) || friendship.getUser2Id().equals(viewedUserId))) {
                isFriend = true;
                break;
            }
        }

        if (isFriend) {
            actionButton.setText("Delete Friend");
            actionButton.setOnAction(event -> {
                for (Friendship friendship : service.getFriendshipsOfUser(currentUserId)) {
                    if ((friendship.getUser1Id().equals(viewedUserId) || friendship.getUser2Id().equals(viewedUserId))) {
                        service.deleteFriendship(friendship.getId());
                        break;
                    }
                }
                loadFriends();
                configureActionButton();
            });
        } else {
            boolean hasSentRequest = false;
            for (Request request : service.getRequests()) {
                if (request.getSenderId().equals(currentUserId) && request.getReceiverId().equals(viewedUserId)) {
                    hasSentRequest = true;
                    break;
                }
            }

            if (hasSentRequest) {
                actionButton.setText("Cancel Request");
                actionButton.setOnAction(event -> {
                    for (Request request : service.getRequests()) {
                        if (request.getSenderId().equals(currentUserId) && request.getReceiverId().equals(viewedUserId)) {
                            service.deleteRequest(request.getId());
                            break;
                        }
                    }
                    configureActionButton();
                });
            } else {
                boolean hasReceivedRequest = false;
                for (Request request : service.getRequests()) {
                    if (request.getSenderId().equals(viewedUserId) && request.getReceiverId().equals(currentUserId)) {
                        hasReceivedRequest = true;
                        break;
                    }
                }

                if (hasReceivedRequest) {
                    actionButton.setText("Accept Request");
                    actionButton.setOnAction(event -> {
                        for (Request request : service.getRequests()) {
                            if (request.getSenderId().equals(viewedUserId) && request.getReceiverId().equals(currentUserId)) {
                                service.addFriendship(request.getSenderId(), request.getReceiverId());
                                service.deleteRequest(request.getId());
                                break;
                            }
                        }
                        loadFriends();
                        configureActionButton();
                    });
                } else {
                    actionButton.setText("Send Request");
                    actionButton.setOnAction(event -> {
                        service.addRequest(currentUserId, viewedUserId);
                        configureActionButton();
                    });
                }
            }
        }
    }


    private void addViewProfileButtonToTable() {
        viewProfileColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    service.setSelectedUserId(selectedUser.getId());
                    onViewProfile.run();
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
    public void update(FriendshipEvent friendshipEvent) {
        // Reload the friends list if a friendship event occurs
        if (friendshipEvent.getType() == EventEnum.RELOAD) {
            loadFriends();
        }
    }
}
