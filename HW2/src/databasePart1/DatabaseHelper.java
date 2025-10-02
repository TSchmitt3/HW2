package databasePart1;

import java.sql.*;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.User;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private Connection connection = null;
    private Statement statement = null;

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTables();  // Create necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20),"
                + "name VARCHAR(255), "
                + "email VARCHAR(255),"
                + "temp_password VARCHAR(255))";
        statement.execute(userTable);

        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE)";
        statement.execute(invitationCodesTable);
    }

    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role, name, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.executeUpdate();
        }
    }

    public String loginWithOTPcheck(String userName, String enteredPw, String role) throws SQLException {
        String query = "SELECT password, temp_password FROM cse360users WHERE userName = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, role);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String realPassword = rs.getString("password");
                String tempPassword = rs.getString("temp_password");

                if (tempPassword != null) {
                    if (enteredPw.equals(tempPassword)) {
                        clearTempPassword(userName);
                        return "temp";
                    } else {
                        return null; // OTP incorrect
                    }
                } else if (enteredPw.equals(realPassword)) {
                    return "normal";
                }
            }
        }
        return null;
    }

    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
	// Updates an existing user in the database
	public void updateUser(User user, String oldUserName) throws SQLException {
		String updateUser = "UPDATE cse360users SET userName=?, password=?, "
				+ "role=?, name=?, email=? WHERE userName=?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getPassword());
	        pstmt.setString(3, user.getRole());
	        pstmt.setString(4, user.getName());
	        pstmt.setString(5, user.getEmail());
	        pstmt.setString(6, oldUserName); // Uses current username as identifier
	        pstmt.executeUpdate();
		}
	}


    public void loadUserDetails(User user) {
        String query = "SELECT name, email FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4);
        String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String generatePassword(String userName) {
        String otp = UUID.randomUUID().toString().substring(0, 8);
        String sql = "UPDATE cse360users SET temp_password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, otp);
            pstmt.setString(2, userName);
            int updated = pstmt.executeUpdate();
            if (updated > 0) return otp;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // add marker to Admin's table for user's who need OTP
    public boolean requestedPw(String userName, String email) {
    	String sql = "UPDATE cse360users SET temp_password = 'PENDING' WHERE userName = ? AND email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, email);
			return pstmt.executeUpdate() > 0;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
 

    public boolean validateOTP(String otp) {
        String query = "SELECT userName FROM cse360users WHERE temp_password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, otp);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("userName");
                clearTempPassword(userName);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearTempPassword(String userName) {
        String query = "UPDATE cse360users SET temp_password = NULL WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserRole(int id, String newRole) throws SQLException {
        String query = "UPDATE cse360users SET role = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newRole);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT * FROM cse360users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String tempPw = rs.getString("temp_password");

                users.add(new User(id, userName, password, role, name, email, tempPw));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void closeConnection() {
        try { if (statement != null) statement.close(); } catch(SQLException se) { se.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch(SQLException se) { se.printStackTrace(); }
    }
}
