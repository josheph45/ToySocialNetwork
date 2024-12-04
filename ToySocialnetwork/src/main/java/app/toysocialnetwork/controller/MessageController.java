package app.toysocialnetwork.controller;

import app.toysocialnetwork.domain.Message;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.MessageEvent;
import app.toysocialnetwork.utils.observer.Observer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class MessageController implements Observer<MessageEvent> {
    private Service service;
    private User receiver;

    @FXML
    private Label conversationTitle;

    @FXML
    private VBox conversationArea;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField newMessageField;

    @FXML
    private Button sendMessageButton;

    /**
     * Set the service for the controller
     * @param service the service to be set
     */
    public void setService(Service service, User receiver) {
        this.service = service;
        this.receiver = receiver;
        conversationTitle.setText("Conversation with " + receiver.getUsername());

        loadConversation();
    }

    /**
     * Load the conversation between the current user and the receiver
     */
    private void loadConversation() {
        Long currentUserId = service.getCurrentUserId();
        Long receiverId = receiver.getId();

        conversationArea.getChildren().clear();

        Iterable<Message> messages = service.getMessagesBetweenUsers(currentUserId, receiverId);

        // Create a DateTimeFormatter to format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        for (Message message : messages) {
            // Create a VBox for each message (to hold date and message text)
            VBox messageContainer = new VBox();
            messageContainer.setSpacing(2); // Add some spacing between date and message text

            // Format the message date (assuming message.getDate() returns a LocalDateTime or Date)
            String formattedDate;
            if (message.getDate() instanceof LocalDateTime) {
                formattedDate = ((LocalDateTime) message.getDate()).format(formatter);
            } else {
                // If the date is of type java.util.Date, you can use SimpleDateFormat
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                formattedDate = dateFormat.format(message.getDate());
            }

            // Add the message date
            Text dateText = new Text(formattedDate);
            dateText.setStyle("-fx-font-size: 10px; -fx-fill: grey;");

            // Add the message text
            Label messageLabel = new Label(message.getText());
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(250);
            messageLabel.setPadding(new Insets(8));
            messageLabel.setStyle("-fx-font-size: 14px; -fx-background-radius: 10px;");

            if (message.getFrom().equals(currentUserId)) {
                // Style for sent messages (align to right)
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-text-fill: white; -fx-background-color: #0084ff;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                // Style for received messages (align to left)
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-text-fill: black; -fx-background-color: #e4e6eb;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            }

            // Add date and message text to the container
            messageContainer.getChildren().addAll(dateText, messageLabel);
            messageContainer.setPadding(new Insets(5, 0, 5, 0)); // Add padding around each message

            conversationArea.getChildren().add(messageContainer);
        }

        // Ensure that the scroll pane scrolls to the bottom to show the most recent message
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }




    /**
     * Initialize the controller
     * Add a listener to the send message button
     */
    @FXML
    public void sendMessage() {
        String text = newMessageField.getText();
        if (text.isEmpty()) {
            return; // Don't send empty messages
        }

        Long currentUserId = service.getCurrentUserId();
        Long receiverId = receiver.getId();

        // Send the message through the service
        service.addMessage(currentUserId, receiverId, text);

        // Clear the message field
        newMessageField.clear();

        // Reload conversation to include the new message
        loadConversation();
    }

    @Override
    public void update(MessageEvent messageEvent) {
        if (messageEvent.getType() == EventEnum.ADD) {
            loadConversation();
        }
    }
}
