package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.Request;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.RequestEvent;
import app.toysocialnetwork.utils.observer.Observer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RequestsController implements Observer<RequestEvent> {
    private Runnable onViewProfile;
    private Service service;
    private final ObservableList<Request> requestsList = FXCollections.observableArrayList();

    @FXML
    private TableView<Request> requestsTableView;

    @FXML
    private TableColumn<Request, String> senderUsernameColumn;

    @FXML
    private TableColumn<Request, Void> viewProfileColumn;

    @FXML
    private TableColumn<Request, Void> acceptColumn;

    @FXML
    private TableColumn<Request, Void> rejectColumn;

    @FXML
    private Button refreshButton;

    /**
     * Sets the service and adds the controller as an observer for request events.
     * Loads the requests from the service.
     * @param service the service to be set
     */
    public void setService(Service service) {
        this.service = service;
        this.service.addRequestObserver(this);
        loadRequests();
    }

    /**
     * Sets the onViewProfile runnable.
     * @param onViewProfile the runnable to be set
     */
    public void setOnViewProfile(Runnable onViewProfile) {
        this.onViewProfile = onViewProfile;
    }

    /**
     * Initializes the columns of the table view.
     */
    @FXML
    public void initialize() {
        senderUsernameColumn.setCellValueFactory(cellData -> {
            Request request = cellData.getValue();
            Long senderId = request.getSenderId();

            String senderUsername = service.getUserById(senderId)
                    .map(User::getUsername)
                    .orElse("Unknown");

            return new SimpleStringProperty(senderUsername);
        });

        addViewProfileButtonToTable();
        addAcceptButtonToTable();
        addRejectButtonToTable();
        refreshButton.setOnAction(event -> loadRequests());
    }

    /**
     * Loads the requests from the service and sets them to the table view.
     */
    private void loadRequests() {
        Iterable<Request> requests = service.getRequestsByReceiver(service.getCurrentUserId());
        requestsList.setAll((List<Request>) requests);
        requestsTableView.setItems(requestsList);
    }

    /**
     * Handles the accept request button action.
     * Deletes the request from the service and adds a friendship between the sender and the receiver.
     * Loads the requests.
     * @param request the request to be accepted
     */
    private void handleAcceptRequest(Request request) {
        service.deleteRequest(request.getId());
        service.addFriendship(request.getSenderId(), request.getReceiverId());
        loadRequests();
    }

    /**
     * Handles the reject request button action.
     * Deletes the request from the service.
     * Loads the requests.
     * @param request the request to be rejected
     */
    private void handleRejectRequest(Request request) {
        service.deleteRequest(request.getId());
        loadRequests();
    }

    /**
     * Adds a view profile button to the table view.
     */
    @FXML
    public void addViewProfileButtonToTable() {
        viewProfileColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View Profile");

            {
                viewButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    if (request != null) {
                        Long senderId = request.getSenderId();
                        service.getUserById(senderId).ifPresent(user -> {
                            service.setSelectedUserId(user.getId());
                            if (onViewProfile != null) {
                                onViewProfile.run();
                            } else {
                                System.err.println("Error: onViewProfile is not set!");
                            }
                        });
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

    /**
     * Adds an accept button to the table view.
     */
    @FXML
    private void addAcceptButtonToTable() {
        acceptColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            {
                acceptButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleAcceptRequest(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptButton);
                }
            }
        });
    }

    /**
     * Adds a reject button to the table view.
     */
    @FXML
    private void addRejectButtonToTable() {
        rejectColumn.setCellFactory(param -> new TableCell<>() {
            private final Button rejectButton = new Button("Reject");

            {
                rejectButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleRejectRequest(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(rejectButton);
                }
            }
        });
    }

    /**
     * Updates the requests list when a request event occurs.
     * @param requestEvent the request event to be handled
     */
    @Override
    public void update(RequestEvent requestEvent) {
        if (requestEvent.getType() == EventEnum.ADD) {
            loadRequests();
        } else if (requestEvent.getType() == EventEnum.DELETE) {
            loadRequests();
        }
    }
}