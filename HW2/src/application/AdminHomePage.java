package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin and allows editing user roles.
 */
@SuppressWarnings("unchecked")
public class AdminHomePage {

    private final DatabaseHelper databaseHelper; 
    private final User currentUser;
	
    public AdminHomePage() {
        this(StatusData.databaseHelper, StatusData.currUser);
    }
	
    public AdminHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
    public void show(Stage primaryStage) {
        show(primaryStage, currentUser.getUserName());
    }

    public void show(Stage primaryStage, String loggedInAdminUserName) {
        BorderPane borderPane = new BorderPane();
        NavigationBar navigationBar = new NavigationBar();
        borderPane.setTop(navigationBar);

        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Welcome label
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Update Account Button
        Button updateAccountButton = new Button("Update Account");
        updateAccountButton.setOnAction(e -> 
            new UpdateAccountPage(databaseHelper, currentUser).show(primaryStage)
        );

        // One-Time Password Reset Section
        Label resetLabel = new Label("Reset password for user: ");
        resetLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-style: italic;");
        
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter user's username");
        Button otpButton = new Button("Generate One-Time Password");
        otpButton.setOnAction(e -> {
            String userName = userNameField.getText();
            if (userName.isEmpty()) {
                resultLabel.setText("Please enter a username");
                return;
            }
            String otp = databaseHelper.generatePassword(userName);
            if (otp != null) {
                resultLabel.setText("One-Time Password for " + userName + ": " + otp);
            } else {
                resultLabel.setText("Error: user not found or update failed.");
            }
        });
        VBox otpSection = new VBox(5, resetLabel, userNameField, otpButton, resultLabel);

        // User Table
        TableView<User> userTable = new TableView<>();
        userTable.setEditable(true);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        //Mark usernames that need passwords
        userNameCol.setCellFactory(col -> new TableCell<User, String>() {
        	@Override
        	protected void updateItem(String item, boolean empty) {
        		super.updateItem(item, empty);
        		if (empty || item == null) {
        			setText(null);
        			setStyle("");
        		}
        		else {
        			setText(item);
        			User user = getTableView().getItems().get(getIndex());
        			
        			if ("PENDING".equals(user.getTempPw())) {
        				setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        			}
        			else {
        				setStyle(""); //reset style
        			}
        		}
        	}
        });
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        
        TableColumn<User, String> pwCol = new TableColumn<>("Password");
        pwCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setCellFactory(col -> new ComboBoxTableCell<>(
            FXCollections.observableArrayList("Admin", "Student", "Reviewer", "Instructor", "Staff")) {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    User user = getTableView().getItems().get(getIndex());
                    setDisable(user.getUserName().equals(loggedInAdminUserName));
                }
            }
        });
        roleCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newRole = event.getNewValue().toLowerCase();
            user.setRole(newRole);
            try {
                DatabaseHelper db = new DatabaseHelper();
                db.connectToDatabase();
                db.updateUserRole(user.getId(), newRole);

                // ✅ Success message
                System.out.println("SUCCESS: Role for user '" + user.getUserName() + "' updated to '" + newRole + "'.");
            } catch (Exception ex) {
                // ❌ Error message
                System.out.println("ERROR: Failed to update role for user '" + user.getUserName() + "'.");
                ex.printStackTrace();
            }
        });

        userTable.getColumns().addAll(idCol, userNameCol, emailCol, pwCol, roleCol);


        try {
            DatabaseHelper db = new DatabaseHelper();
            db.connectToDatabase();
            ObservableList<User> users = db.getAllUsers();
            userTable.setItems(users);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Assemble everything
        layout.getChildren().addAll(adminLabel, updateAccountButton, otpSection, userTable);
        borderPane.setCenter(layout);

        Scene adminScene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
        primaryStage.show();
    }
}
