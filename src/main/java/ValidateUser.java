import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ValidateUser")
public class ValidateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Expires", "0");
		response.setDateHeader("Expires", -1);

		PrintWriter out = response.getWriter();

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// Database connection parameters
		String url = "jdbc:mysql://localhost:3306/patient_management_system";
		String dbUsername = "root";
		String dbPassword = "root";

		String selectSql = "SELECT * FROM user WHERE username = ? AND password = ?";
		String updateSql = "UPDATE user SET status = 'Active' WHERE username = ?";

		try {
			// Load JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Establish database connection
			Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

			// Prepare SQL statement for selecting user
			PreparedStatement selectStatement = connection.prepareStatement(selectSql);
			selectStatement.setString(1, username);
			selectStatement.setString(2, password);

			// Execute SQL query for selecting user
			ResultSet result = selectStatement.executeQuery();

			if (result.next()) {
				// User credentials are valid
				String utype = result.getString("user_type");
				String uname = result.getString("username");
				String uid = result.getString("user_id");

				// set user name and user type values on session variable
				HttpSession session = request.getSession();
				session.setAttribute("utype", utype);
				session.setAttribute("uname", uname);
				session.setAttribute("user_id", uid);

				// Update status to Active
				PreparedStatement updateStatement = connection.prepareStatement(updateSql);
				updateStatement.setString(1, username);
				updateStatement.executeUpdate();

				// Redirect users based on user type
				if (utype.equals("Reception")) {
					response.sendRedirect("Reception_home");
				} else if (utype.equals("Doctor")) {
					response.sendRedirect("Doctor_home");
				} else if (utype.equals("Physician_Assistant")) {
					response.sendRedirect("PhysicianAss_home");
				} else if (utype.equals("Lab_Technician")) {
					response.sendRedirect("LabTech_home");
				} else {
					// Handle other user types or scenarios
					out.println("Invalid user type");
				}
			} else {
				// Invalid credentials
				out.print("Invalid username or password");
				out.print(" <a href=Home>try again</a>");
			}

			// Close database connection
			connection.close();

		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Handle GET requests, maybe redirect to a login page
		response.sendRedirect("Home");
	}
}
