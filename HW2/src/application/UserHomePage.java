package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {

	private final DatabaseHelper databaseHelper;
	private final User currentUser;
	
	// Receives the logged-in user and DB helper
	public UserHomePage() {
		this(StatusData.databaseHelper, StatusData.currUser);
	}
	
	public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
	    this.databaseHelper = databaseHelper;
	    this.currentUser = currentUser;
	}
	
    public void show(Stage primaryStage) {
    	BorderPane borderPane = new BorderPane();
    	
    	NavigationBar navBar = new NavigationBar();
	    
	    borderPane.setTop(navBar);
    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    //Update Account Button
	    Button updateAccountButton = new Button("Update Account");
	    updateAccountButton.setOnAction(e -> {
	    	new UpdateAccountPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    layout.getChildren().addAll(userLabel, updateAccountButton);
	    borderPane.setCenter(layout);
	    Scene userScene = new Scene(borderPane, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
	    primaryStage.show();
    	
    }
}