import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Test_result")
public class Test_result extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String search = request.getParameter("search");
		String searchQuery = request.getParameter("search_query");
		String send = request.getParameter("send");

		String test = request.getParameter("testResult");
		String selectedPatient = request.getParameter("selectedPatient");
	    String testRequest = request.getParameter("testRequest");


		out.println("<html>");
		out.println("<head>");
		out.println("<title> Test Result</title>");
		out.println("<link rel=stylesheet type=text/css href=css/update.css>");
		out.println("<link rel=stylesheet type=text/css href=css/assign.css>");
		out.println("</head>");
		out.println("<body>");

		RequestDispatcher rd = request.getRequestDispatcher("header");
		rd.include(request, response);

		out.print("<div class=back-search>");
		out.print("<a href='LabTech_home'>Back</a>");
		out.print("<div class=search>");
		out.println("<form class=Search-form action=Test_result method=post>");
		out.println("<input type=text name=search_query placeholder=Patient ID or Name required>");
		out.println("<button class=search-btn name=search>Search</button>");
		out.println("</form>");
		out.println("</div>");
		out.println("</div>");

		try {
			if (search == null) {
				displayAllPatients(out);
			} else {
				displaySearchResults(out, searchQuery);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			out.println("<span>Error: " + e.getMessage() + "</span>");
		}
		if (send != null) {

			if (selectedPatient == null || test == null) {
				out.print("<script>alert('Error: missing data.');</script>");
				return;
			}
			sendTestResult(out, selectedPatient, test, testRequest);

		}

		out.println("</body>");
		out.println("</html>");
	}

	private void displayAllPatients(PrintWriter out) throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.cj.jdbc.Driver";
		String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
		String dbUsername = "root";
		String dbPassword = "root";

		Class.forName(driverName);
		try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			String sql = "SELECT * FROM patient_profile pp JOIN laboratory_test ad ON pp.patient_id = ad.patient_id";
			try (PreparedStatement stmt = conn.prepareStatement(sql); 
					ResultSet rs = stmt.executeQuery()) {
				displayPatientsTable(out, rs);
			}
		}
	}

	private void displaySearchResults(PrintWriter out, String searchQuery) throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.cj.jdbc.Driver";
		String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
		String dbUsername = "root";
		String dbPassword = "root";

		Class.forName(driverName);
		try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			String sql = "SELECT * FROM patient_profile pp JOIN laboratory_test ad ON pp.patient_id = ad.patient_id WHERE pp.patient_id=? OR pp.first_name LIKE ? OR pp.second_name LIKE ?";

			try (PreparedStatement searchStmt = conn.prepareStatement(sql)) {
				searchStmt.setString(1, searchQuery);
				searchStmt.setString(2, "%" + searchQuery + "%");
				searchStmt.setString(3, "%" + searchQuery + "%");
				try (ResultSet rs = searchStmt.executeQuery()) {
					displayPatientsTable(out, rs);
				}
			}
		}
	}

	private void displayPatientsTable(PrintWriter out, ResultSet rs) throws SQLException {

	    // Check if the result set is empty
	    if (!rs.isBeforeFirst()) {
	        out.println("<p>No patients found.</p>");
	        return; // No need to display the table header if there are no patients
	    }

	    out.print("<div class=test-form-wrapper>");
	    out.println("<div class=assigned_patients>");
	    out.print("<form class=test-form action='Test_result' method=post>");
	    out.println("<h2>Test Requests</h2>");
	    out.println("<table border=1>");
	    out.println("<tr>");
	    out.print("<th>Select</th>");
	    out.println("<th>Patient Id</th>");
	    out.println("<th>Test Request</th>");
	    
	    out.println("</tr>");
	    while (rs.next()) {
	        String patientId = rs.getString("patient_id");
	        String testRequest = rs.getString("test_request"); 
	        out.println("<tr>");
	        out.print("<td><input type=radio name=selectedPatient value=" + patientId + "></td>");
	        out.println("<td>" + patientId + "</td>");
	        out.println("<td>" + testRequest + "</td>");
	        out.println("<input type='hidden' name='testRequest' value='" + testRequest + "'>"); // Add hidden input for test request
	       
	        out.println("</tr>");
	    }
	    out.println("</table>");
	    out.print("<div class=form-cnt>");
	    out.print("<h3>Send Test Result</h3>");
	    out.print("<div class=form>");

	    out.println("<input type='text' name='testResult'>");
	    out.print("<input class=send type='submit' name='send' value='Send'>");
	    out.print("</div>");
	    out.print("</div>");
	    out.print("</form>");
	    out.print("</div>");
	    out.print("</div>");

	}

	private void sendTestResult(PrintWriter out, String selectedPatient, String test, String testRequest) {
	    String sql = "UPDATE laboratory_test SET test_result = ? WHERE patient_id = ? AND test_request = ?";

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
	                "root", "root"); PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, test); // Set the test result
	            stmt.setString(2, selectedPatient); // Set the patient ID
	            stmt.setString(3, testRequest); // Set the test request

	            int rowsAffected = stmt.executeUpdate();
	            if (rowsAffected > 0) {
	                out.print("<script>alert('Test result sent successfully.');</script>");
	            } else {
	                out.print("<script>alert('Failed to send result.');</script>");
	            }
	        }
	    } catch (ClassNotFoundException | SQLException e) {
	        out.print("<script>alert('Error: " + e.getMessage() + "');</script>");
	        e.printStackTrace();
	    }
	}

}
