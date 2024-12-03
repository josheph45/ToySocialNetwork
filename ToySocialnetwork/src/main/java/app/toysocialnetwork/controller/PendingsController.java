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

public class PendingsController implements Observer<RequestEvent> {
    private Runnable onViewProfile;
    private Service service;
    private final ObservableList<Request> pendingsList = FXCollections.observableArrayList();

    @FXML
    private TableView<Request> pendingsTableView;

    @FXML
    private TableColumn<Request, String> receiverUsernameColumn;

    @FXML
    private TableColumn<Request, Void> viewColumn;

    @FXML
    private TableColumn<Request, Void> deleteColumn;

    @FXML
    private Button refreshButton;

    /**
     * Set the service for the controller.
     * @param service the service to be set
     */
    public void setService(Service service) {
        this.service = service;
        this.service.addRequestObserver(this);
        loadPendings();
    }

    /**
     * Set the onViewProfile runnable.
     * @param onViewProfile the runnable to be set
     */
    public void setOnViewProfile(Runnable onViewProfile) {
        this.onViewProfile = onViewProfile;
    }

    /**
     * Initialize the controller.
     * Add view profile and delete buttons to the table.
     */
    @FXML
    public void initialize() {
        receiverUsernameColumn.setCellValueFactory(cellData -> {
            Request request = cellData.getValue();
            Long receiverId = request.getReceiverId();

            String receiverUsername = service.getUserById(receiverId)
                    .map(User::getUsername)
                    .orElse("Unknown");

            return new SimpleStringProperty(receiverUsername);
        });

        addViewProfileButtonToTable();
        addDeleteButtonToTable();
        refreshButton.setOnAction(event -> loadPendings());
    }

    /**
     * Load the pending requests.
     */
    private void loadPendings() {
        Iterable<Request> requests = service.getRequestsToUser(service.getCurrentUserId());
        pendingsList.setAll((List<Request>) requests);
        pendingsTableView.setItems(pendingsList);
    }

    /**
     * Handle the delete request action.
     * @param request the request to be deleted
     */
    private void handleDeleteRequest(Request request) {
        service.deleteRequest(request.getId());
        loadPendings();
    }

    /**
     * Add view profile button to the table.
     */
    @FXML
    private void addViewProfileButtonToTable() {
        viewColumn.setCellFactory(param -> new TableCell<Request, Void>() {
            private final Button viewButton = new Button("View");
            {
                viewButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    if (request != null) {
                        Long receiverId = request.getReceiverId();
                        service.getUserById(receiverId).ifPresent(user -> {
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
     * Add delete button to the table.
     */
    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleDeleteRequest(request);
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
     * Update the controller.
     * @param event the event to be handled
     */
    @Override
    public void update(RequestEvent event) {
        if (event.getType() == EventEnum.DELETE) {
            loadPendings();
        }
    }
}

