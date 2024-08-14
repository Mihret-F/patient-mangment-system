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
import javax.servlet.http.HttpSession;

@WebServlet("/View_testResult")
public class View_testResult extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String search = request.getParameter("search");
        String searchQuery = request.getParameter("search_query");

        out.print("<html>");
        out.print("<head>");
        out.print("<title>View Assigned Patients</title>");
        out.print("<link rel=stylesheet type=text/css href=css/update.css>");
        out.print("<link rel=stylesheet type=text/css href=css/assign.css>");
        out.print("</head>");
        out.print("<body>");

        RequestDispatcher rd = request.getRequestDispatcher("header");
        rd.include(request, response);

        out.print("<div class=back-search>");
        out.print("<a class='back' href='Doctor_home'>Back</a>");
        out.print("<div class=search>");
        out.print("<form class=Search-form action=View_testResult method=post>");
        out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
        out.print("<button name=search class=search-btn>Search</button>");
        out.print("</form>");
        out.print("</div>");
        out.print("</div>");

        out.print("<a class='medlink' href='Add_medicalRecord'>Add Medical Record</a>");

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("user_id");

        // Display all patients initially

		try {
			if (search == null) {
				displayAllPatients(out, userId);
			} else {
				displaySearchResults(out, searchQuery, userId);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			out.println("<span>Error: " + e.getMessage() + "</span>");
		}

        out.print("</body>");
        out.print("</html>");
    }

    private void displayAllPatients(PrintWriter out, String userId) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM patient_profile pp " +
                "JOIN laboratory_test ad ON pp.patient_id = ad.patient_id " +
                "JOIN assigned_doctor adoc ON pp.patient_id = adoc.patient_id " +
                "WHERE adoc.doctor_id = ?";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                "root", "root");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
				displayPatientsTable(out, rs);
            }
        }
    }

    private void displaySearchResults(PrintWriter out, String searchQuery, String userId) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM patient_profile pp " +
                "JOIN laboratory_test ad ON pp.patient_id = ad.patient_id " +
                "JOIN assigned_doctor adoc ON pp.patient_id = adoc.patient_id " +
                "WHERE (pp.patient_id=? OR pp.first_name LIKE ? OR pp.second_name LIKE ?) " +
                "AND adoc.doctor_id = ?";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                "root", "root");
             PreparedStatement searchStmt = conn.prepareStatement(sql)) {
            searchStmt.setString(1, searchQuery);
            searchStmt.setString(2, "%" + searchQuery + "%");
            searchStmt.setString(3, "%" + searchQuery + "%");
            searchStmt.setString(4, userId);

            try (ResultSet rs = searchStmt.executeQuery()) {
				displayPatientsTable(out, rs);
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
        out.print("<div class=assigned_patients>");
        out.print("<div class=test-form>");        
        out.print("<table border=1>");
		out.println("<h2>Test Results</h2>");
        out.print("<tr>");
        out.print("<th>Patient Id</th>");
        out.print("<th>Test Request</th>");
        out.print("<th>Test Result</th>");
        out.print("</tr>");
        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("patient_id") + "</td>");
            out.print("<td>" + rs.getString("test_request") + "</td>");
            out.print("<td>" + rs.getString("test_result") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");
        out.print("</div>");
        out.print("</div>");
        out.print("</div>");
    }
}

