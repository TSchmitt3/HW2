package application;

import javafx.scene.control.ToolBar;

import java.sql.SQLException;

import javafx.scene.control.Button;

public class NavigationBar extends ToolBar {
	public NavigationBar() {
		Button welcomePageButton = new Button("Welcome Page");
		welcomePageButton.setOnAction(_ -> {
			new WelcomeLoginPage(StatusData.databaseHelper).show(StatusData.primaryStage, StatusData.currUser);
		});
		this.getItems().add(welcomePageButton);

		if (StatusData.currUser.getRole().equals("user")) {
			Button userHomePageButton;
			userHomePageButton = new Button("User Home Page");
			userHomePageButton.setOnAction(_ -> {
				new UserHomePage().show(StatusData.primaryStage);
			});
			
			this.getItems().add(userHomePageButton);
		}
		
		if (StatusData.currUser.getRole().equals("admin")) {
			Button adminHomePageButton = new Button("Admin Home Page");
			adminHomePageButton.setOnAction(_ -> {
				new AdminHomePage().show(StatusData.primaryStage);
			});
			
			this.getItems().add(adminHomePageButton);
		}
		
		if (StatusData.currUser.getRole().equals("admin")) {
			Button invitationPageButton = new Button("Invitation Page");
			invitationPageButton.setOnAction(_ -> {
				new InvitationPage().show(StatusData.databaseHelper, StatusData.primaryStage);
			});
			
			this.getItems().add(invitationPageButton);
		}
		
		Button logOutButton = new Button("Log out");
	    logOutButton.setOnAction(_ -> {
	    	try {
	    		StatusData.databaseHelper.closeConnection();
	    		StatusData.databaseHelper.connectToDatabase();
	    		StatusData.currUser = null; 
	    		} catch (SQLException e) { }
	    	new SetupLoginSelectionPage(StatusData.databaseHelper).show(StatusData.primaryStage);
	    	});	
	    this.getItems().add(logOutButton);
	}
}
