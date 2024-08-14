

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Pragma","no-cache"); 
		response.setHeader("Cache-Control","no-store"); 
		response.setHeader("Expires","0"); 
		response.setDateHeader("Expires",-1); 

		 HttpSession session = request.getSession(true);
		 

	        if (session != null) {
	            String username = (String) session.getAttribute("uname"); 
	            session.invalidate(); 
	            
	            // Update the status to "inactive" in the database
	            try {
					updateStatusInDatabase(username, "inactive");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	            response.sendRedirect("Home");
	        } else {
	            // Session doesn't exist, redirect to home page
	            response.sendRedirect("Home");
	        }
	    }
	    private void updateStatusInDatabase(String username, String status) throws ClassNotFoundException {
	        // Database connection parameters
	        String url = "jdbc:mysql://localhost:3306/patient_management_system";
	        String dbUsername = "root";
	        String dbPassword = "root";

	        // SQL query to update status
	        String sql = "UPDATE user SET status = ? WHERE username = ?";

	        try {
	            // Load JDBC driver and establish database connection
	            Class.forName("com.mysql.jdbc.Driver");
	            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

	            // Prepare SQL statement
	            PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, status);
	            statement.setString(2, username);

	            // Execute SQL query
	            statement.executeUpdate();

	            // Close database connection
	            connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}