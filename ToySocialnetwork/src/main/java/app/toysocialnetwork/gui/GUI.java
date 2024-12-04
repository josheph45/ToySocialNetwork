package app.toysocialnetwork.gui;

import app.toysocialnetwork.controller.*;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.service.Service;
import app.toysocialnetwork.repository.database.*;
import app.toysocialnetwork.domain.validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        UserDBRepository userRepo = new UserDBRepository(new UserValidation());
        FriendshipDBRepository friendshipRepo = new FriendshipDBRepository(new FriendshipValidation());
        RequestDBRepository requestRepo = new RequestDBRepository(new RequestValidation());
        MessageDBRepository messageRepo = new MessageDBRepository(new MessageValidation());

        Service service = new Service(userRepo, friendshipRepo, requestRepo, messageRepo);

        openLoginWindow(primaryStage, service);
    }

    private AnchorPane loadLoginView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/login-view.fxml"));

        // Load the view
        AnchorPane loginView = loader.load();

        // Set up the controller
        LoginController loginController = loader.getController();
        loginController.setService(service);
        loginController.setOnLogin(() -> {
            openMainWindow((Stage) loginView.getScene().getWindow(), service);
        });

        return loginView;
    }

    private AnchorPane loadRegisterView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/register-view.fxml"));

        // Load the view
        AnchorPane registerView = loader.load();

        // Set up the controller
        RegisterController registerController = loader.getController();
        registerController.setService(service);

        return registerView;
    }

    private AnchorPane loadUsersView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/users-view.fxml"));

        // Load the view
        AnchorPane usersView = loader.load();

        // Set up the controller
        UsersController usersController = loader.getController();
        usersController.setService(service);
        usersController.setOnViewProfile(() -> {
            openProfileWindow((Stage) usersView.getScene().getWindow(), service);
        });

        return usersView;
    }

    private AnchorPane loadFriendsView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/friends-view.fxml"));

        // Load the view
        AnchorPane friendsView = loader.load();

        // Set up the controller
        FriendsController friendsController = loader.getController();
        friendsController.setService(service);
        friendsController.setOnMessage(() -> {
            openMessageWindow((Stage) friendsView.getScene().getWindow(), service);
        });

        return friendsView;
    }

    private AnchorPane loadRequestsView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/requests-view.fxml"));

        // Load the view
        AnchorPane requestsView = loader.load();

        // Set up the controller
        RequestsController requestsController = loader.getController();
        requestsController.setService(service);
        requestsController.setOnViewProfile(() -> {
            openProfileWindow((Stage) requestsView.getScene().getWindow(), service);
        });

        return requestsView;
    }

    private AnchorPane loadPendingsView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/pendings-view.fxml"));

        // Load the view
        AnchorPane pendingsView = loader.load();

        // Set up the controller
        PendingsController pendingsController = loader.getController();
        pendingsController.setService(service);
        pendingsController.setOnViewProfile(() -> {
            openProfileWindow((Stage) pendingsView.getScene().getWindow(), service);
        });

        return pendingsView;
    }

    private AnchorPane loadMainView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/main-view.fxml"));

        // Load the view
        AnchorPane mainView = loader.load();

        // Set up the controller
        MainController mainController = loader.getController();
        mainController.setService(service);
        mainController.setOnLogOut(() -> {
            openLoginWindow((Stage) mainView.getScene().getWindow(), service);
        });
        mainController.setOnUsers(() -> {
            openUsersWindow((Stage) mainView.getScene().getWindow(), service);
        });
        mainController.setOnFriends(() -> {
            openFriendsWindow((Stage) mainView.getScene().getWindow(), service);
        });
        mainController.setOnRequests(() -> {
            openRequestsWindow((Stage) mainView.getScene().getWindow(), service);
        });
        mainController.setOnPendings(() -> {
            openPendingsWindow((Stage) mainView.getScene().getWindow(), service);
        });

        return mainView;
    }

    private AnchorPane loadProfileView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/profile-view.fxml"));

        // Load the view
        AnchorPane profileView = loader.load();

        // Set up the controller
        ProfileController profileController = loader.getController();
        User selectedUser = service.getUserById(service.getSelectedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        profileController.setService(service, selectedUser);
        profileController.setOnViewProfile(() -> {
            openProfileWindow((Stage) profileView.getScene().getWindow(), service);
        });

        return profileView;
    }

    private AnchorPane loadMessageView(Service service) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/toysocialnetwork/view/message-view.fxml"));

        // Load the view
        AnchorPane messageView = loader.load();

        // Set up the controller
        MessageController messageController = loader.getController();
        User selectedUser = service.getUserById(service.getSelectedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        messageController.setService(service, selectedUser);

        return messageView;
    }

    private void openUsersWindow(Stage stage, Service service) {
        try {
            // add the users view to the stage, to the already existing tabs
            AnchorPane usersView = loadUsersView(service);
            Tab usersTab = new Tab("Users");
            usersTab.setContent(usersView);
            usersTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(usersTab);

            // select the users tab
            tabPane.getSelectionModel().select(usersTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFriendsWindow(Stage stage, Service service) {
        try {
            // add the friends view to the stage, to the already existing tabs
            AnchorPane friendsView = loadFriendsView(service);
            Tab friendsTab = new Tab("Friends");
            friendsTab.setContent(friendsView);
            friendsTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(friendsTab);

            // select the friends tab
            tabPane.getSelectionModel().select(friendsTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRequestsWindow(Stage stage, Service service) {
        try {
            // add the requests view to the stage, to the already existing tabs
            AnchorPane requestsView = loadRequestsView(service);
            Tab requestsTab = new Tab("Requests");
            requestsTab.setContent(requestsView);
            requestsTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(requestsTab);

            // select the requests tab
            tabPane.getSelectionModel().select(requestsTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPendingsWindow(Stage stage, Service service) {
        try {
            // add the pendings view to the stage, to the already existing tabs
            AnchorPane pendingsView = loadPendingsView(service);
            Tab pendingsTab = new Tab("Pendings");
            pendingsTab.setContent(pendingsView);
            pendingsTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(pendingsTab);

            // select the pendings tab
            tabPane.getSelectionModel().select(pendingsTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openProfileWindow(Stage stage, Service service) {
        try {
            // add the profile view to the stage, to the already existing tabs
            AnchorPane profileView = loadProfileView(service);
            Tab profileTab = new Tab("Profile");
            profileTab.setContent(profileView);
            profileTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(profileTab);

            // select the profile tab
            tabPane.getSelectionModel().select(profileTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMessageWindow(Stage stage, Service service) {
        try {
            // add the message view to the stage, to the already existing tabs
            AnchorPane messageView = loadMessageView(service);
            Tab messageTab = new Tab("Message");
            messageTab.setContent(messageView);
            messageTab.setClosable(true);

            TabPane tabPane = (TabPane) stage.getScene().getRoot();
            tabPane.getTabs().add(messageTab);

            // select the message tab
            tabPane.getSelectionModel().select(messageTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openLoginWindow(Stage primaryStage, Service service) {
        try {
            // Create TabPane
            TabPane tabPane = new TabPane();

            // Create Login Tab
            Tab loginTab = new Tab("Login");
            loginTab.setContent(loadLoginView(service));
            loginTab.setClosable(false);

            // Create Register Tab
            Tab registerTab = new Tab("Register");
            registerTab.setContent(loadRegisterView(service));
            registerTab.setClosable(false);

            // Add tabs to TabPane
            tabPane.getTabs().addAll(loginTab, registerTab);

            // Set up the scene
            Scene scene = new Scene(tabPane, 600, 400);
            primaryStage.setTitle("Toy Social Network");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainWindow(Stage primaryStage, Service service) {
        try {
            // Load the main view
            AnchorPane mainView = loadMainView(service);

            // Create tabs
            Tab mainTab = new Tab("Main");
            mainTab.setContent(mainView);
            mainTab.setClosable(false);

            // Create the TabPane and add the tabs
            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(mainTab);

            // Create the scene and show the stage
            Scene scene = new Scene(tabPane, 600, 400);
            primaryStage.setTitle("Toy Social Network");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
