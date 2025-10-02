package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

public class SetupAccountPage {

    private final DatabaseHelper databaseHelper;

    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// --- Back button ---
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        HBox topBar = new HBox(backBtn);
        topBar.setStyle("-fx-padding: 10; -fx-alignment: top-left;");

        // --- Input fields ---
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter E-mail address");
        emailField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);

        VBox inputBox = new VBox(10, userNameField, passwordField, emailField, inviteCodeField);
        inputBox.setStyle("-fx-alignment: center;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // --- Setup button ---
        Button setupButton = new Button("Setup");
        setupButton.setOnAction(a -> handleSetup(userNameField, passwordField, emailField, inviteCodeField, errorLabel, primaryStage));

        VBox setupBox = new VBox(10, inputBox, setupButton, errorLabel);
        setupBox.setStyle("-fx-alignment: center;");

        // --- Requirements labels ---
        Label requirementsHeader = new Label("Username and Password Requirements:");
        requirementsHeader.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: green;");
        Label usernameRules = new Label("- Username must start with a letter and be 4–16 characters.");
        Label passwordRules1 = new Label("- Password must be at least 8 characters.");
        Label passwordRules2 = new Label("- Must contain: uppercase, lowercase, digit, special character.");
        usernameRules.setStyle("-fx-font-size: 12; -fx-text-fill: green;");
        passwordRules1.setStyle("-fx-font-size: 12; -fx-text-fill: green;");
        passwordRules2.setStyle("-fx-font-size: 12; -fx-text-fill: green;");

        VBox requirementsBox = new VBox(5, requirementsHeader, usernameRules, passwordRules1, passwordRules2);
        requirementsBox.setStyle("-fx-alignment: center-left; -fx-padding: 10;");

        // --- Main layout ---
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setCenter(setupBox);
        mainLayout.setBottom(requirementsBox);
        mainLayout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(mainLayout, 800, 500));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }

    private void handleSetup(TextField userNameField, PasswordField passwordField, TextField emailField, TextField inviteCodeField, Label errorLabel, Stage primaryStage) {
        try {
            String userName = userNameField.getText();
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);
            if (!userNameError.isEmpty()) {
                errorLabel.setText(userNameError);
                return;
            }

            String password = passwordField.getText();
            String passwordError = PasswordRecognizer.evaluatePassword(password);
            if (!passwordError.isEmpty()) {
                errorLabel.setText(passwordError);
                return;
            }

            String email = emailField.getText();
            String emailError = EmailRecognizer.validate(email);
            if (!emailError.isEmpty()) {
                errorLabel.setText(emailError);
                return;
            }

            String code = inviteCodeField.getText();

            if (!databaseHelper.doesUserExist(userName)) {
                if (databaseHelper.validateInvitationCode(code)) {
                    User user = new User(userName, password, "user");
                    StatusData.currUser = user;
                    databaseHelper.register(user);
                    new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                } else {
                    errorLabel.setText("Please enter a valid invitation code");
                }
            } else {
                errorLabel.setText("This Username is taken! Please choose another.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Database error occurred.");
        }
    }
}
