package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

public class UserLoginPage {

    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // --- Back button ---
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        HBox topBar = new HBox(backBtn);
        topBar.setStyle("-fx-padding: 10; -fx-alignment: top-left;");

        // --- Username and Password fields ---
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        VBox inputBox = new VBox(10, userNameField, passwordField);
        inputBox.setStyle("-fx-alignment: center;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // --- Login and Forgot Password buttons ---
        Button loginButton = new Button("Login");
        loginButton.setOnAction(a -> handleLogin(userNameField, passwordField, errorLabel, primaryStage));

        Button resetPwButton = new Button("Forgot Password?");
        resetPwButton.setOnAction(e -> openPasswordResetPopup());

        HBox buttonBox = new HBox(10, loginButton, resetPwButton);
        buttonBox.setStyle("-fx-alignment: center;");

        VBox centerBox = new VBox(15, inputBox, buttonBox, errorLabel);
        centerBox.setStyle("-fx-alignment: center;");

        // --- Main layout ---
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(centerBox);
        mainLayout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(mainLayout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
    
    	// --- METHODS ---

    private void handleLogin(TextField userNameField, PasswordField passwordField, Label errorLabel, Stage primaryStage) {
        String userName = userNameField.getText();
        String password = passwordField.getText();

        WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);

        try {
            String role = databaseHelper.getUserRole(userName);
            if (role == null) {
                errorLabel.setText("User account doesn't exist.");
                return;
            }

            String loginResult = databaseHelper.loginWithOTPcheck(userName, password, role);

            if ("normal".equals(loginResult)) {
                User user = new User(userName, password, role);
                databaseHelper.loadUserDetails(user);
                StatusData.currUser = user;
                welcomeLoginPage.show(StatusData.primaryStage, user);

            } else if ("temp".equals(loginResult)) {
                User user = new User(userName, password, role);
                StatusData.currUser = user;
                errorLabel.setText("Please reset your password.");
                ResetPasswordPage resetPasswordPage = new ResetPasswordPage(databaseHelper, user);
                resetPasswordPage.show(StatusData.primaryStage, userName);

            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (SQLException ex) {
            errorLabel.setText("Database error during login.");
            ex.printStackTrace();
        }
    }

    private void openPasswordResetPopup() {
        Stage popup = new Stage();
        popup.setTitle("Password Reset");

        Label label1 = new Label("Please enter your username");
        label1.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");

        TextField unField = new TextField();
        unField.setPromptText("Enter your username");

        Label label2 = new Label("Please enter your email");
        label2.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(ev -> {
            String enteredUser = unField.getText();
            String enteredEmail = emailField.getText();

            if (enteredUser.isEmpty() || enteredEmail.isEmpty()) {
                messageLabel.setText("Both fields must be filled.");
                return;
            }

            String role = databaseHelper.getUserRole(enteredUser);
            if (role == null) {
                messageLabel.setText("Username does not exist.");
                return;
            }

            User user = new User(enteredUser, "", role);
            databaseHelper.loadUserDetails(user);

            if (enteredEmail.equals(user.getEmail())) {
                messageLabel.setStyle("-fx-text-fill: green;");
                if (databaseHelper.requestedPw(enteredUser, enteredEmail)) {
                    messageLabel.setText("Check your email for a one-time reset code.");
                } else {
                    messageLabel.setText("Database error: request failed.");
                }
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Email does not match username.");
            }
        });

        VBox layout = new VBox(10,
                new Label("Confirm your account:"),
                label1, unField,
                label2, emailField,
                submitButton, messageLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center-left;");

        popup.setScene(new Scene(layout, 400, 250));
        popup.show();
    }
}
