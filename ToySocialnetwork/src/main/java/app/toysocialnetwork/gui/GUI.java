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

        Service service = new Service(userRepo, friendshipRepo, requestRepo);

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

            // Load the users view
            AnchorPane usersView = loadUsersView(service);

            // Load the friends view
            AnchorPane friendsView = loadFriendsView(service);

            // Load the requests view
            AnchorPane requestsView = loadRequestsView(service);

            // Load the pendings view
            AnchorPane pendingsView = loadPendingsView(service);

            // Create tabs
            Tab mainTab = new Tab("Main");
            mainTab.setContent(mainView);
            mainTab.setClosable(false);

            Tab usersTab = new Tab("Users");
            usersTab.setContent(usersView);
            usersTab.setClosable(false);

            Tab friendsTab = new Tab("Friends");
            friendsTab.setContent(friendsView);
            friendsTab.setClosable(false);

            Tab requestsTab = new Tab("Requests");
            requestsTab.setContent(requestsView);
            requestsTab.setClosable(false);

            Tab pendingsTab = new Tab("Pendings");
            pendingsTab.setContent(pendingsView);
            pendingsTab.setClosable(false);

            // Create the TabPane and add the tabs
            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(mainTab, usersTab, friendsTab, requestsTab, pendingsTab);

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
