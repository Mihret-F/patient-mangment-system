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

@WebServlet("/Add_medicalRecord")
public class Add_medicalRecord extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String search = request.getParameter("search");
		String searchQuery = request.getParameter("search_query");
		String record = request.getParameter("record");
		String medicalRecord = request.getParameter("medicalRecord");
		String date = request.getParameter("date");
		String selectedPatient = request.getParameter("selectedPatient");

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
		out.print("<a class='back' href='View_testResult'>Back</a>");
		out.print("<div class=search>");
		out.print("<form class=Search-form action='' method=get>");
		out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
		out.print("<button name=search class=search-btn>Search</button>");
		out.print("</form>");
		out.print("</div>");
		out.print("</div>");
        out.print("<a class='medlink' href='View_medicalRecord'>View Medical Record</a>");


        try {
			if (search == null) {
				displayAllPatients(out);
			} else {
				displaySearchResults(out, searchQuery);
			}
		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
			out.println(e.getMessage());		}
		if (record != null) {

			if (selectedPatient == null || medicalRecord == null || date.isEmpty()) {
				out.print("<script>alert('Error: missing data.');</script>");
				return;
			}
			sendMedicalRecord(out, selectedPatient, medicalRecord, date);

		}
		out.print("</body>");
		out.print("</html>");
	}

    public void sendMedicalRecord(PrintWriter out, String selectedPatient, String medicalRecord, String date) {

        String insertQuery = "INSERT INTO patient_history (patient_id, medical_record, date) " + "VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                    "root", "root");
                    PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, selectedPatient);
                stmt.setString(2, medicalRecord);
                stmt.setString(3, date);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    out.print("<script>alert('Medical record added successfully.');</script>");
                } else {
                    out.print("<script>alert('Failed to add medical record.');</script>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            out.print("<script>alert('Error: " + e.getMessage() + "');</script>");
            e.printStackTrace();
        }
    }

	private void displaySearchResults(PrintWriter out, String searchQuery) throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.cj.jdbc.Driver";
		String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
		String dbusername = "root";
		String dbpassword = "root";

		Class.forName(driverName);
		try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {

			String sql = "SELECT * FROM patient_profile pp JOIN laboratory_test ad ON pp.patient_id = ad.patient_id WHERE (pp.patient_id=? OR pp.first_name LIKE ? OR pp.second_name LIKE ?)";
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

	private void displayAllPatients(PrintWriter out) throws ClassNotFoundException, SQLException {
		String sql = "SELECT * FROM patient_profile pp JOIN laboratory_test ad ON pp.patient_id = ad.patient_id";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
					"root", "root");
					PreparedStatement stmt = conn.prepareStatement(sql);
					ResultSet rs = stmt.executeQuery()) {
				displayPatientsTable(out, rs);

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void displayPatientsTable(PrintWriter out, ResultSet rs) throws SQLException {
		
		 // Check if the result set is empty
	    if (!rs.isBeforeFirst()) {
	        out.println("<p>No patients found.</p>");
	        return; // No need to display the table header if there are no patients
	    }
		
	    out.print("<div class=test-form-wrapper>");                	
	    out.print("<form class=test-form action='Add_medicalRecord' method=post>");

		out.print("<div class=assigned_patients>");
		out.print("<table border=1>");
		out.print("<tr>");
		out.print("<th>Select</th>");
		out.print("<th>Patient Id</th>");
		out.print("<th>Name</th>");
		out.print("</tr>");
		while (rs.next()) {
			out.print("<tr>");
			out.print("<td><input type=radio name=selectedPatient value=" + rs.getString("patient_id") + "></td>");
			out.print("<td>" + rs.getString("patient_id") + "</td>");
			out.print("<td>" + rs.getString("first_name") + "" + rs.getString("second_name") + "</td>");
			out.print("</tr>");
		}
		out.print("</table>");

		out.print("<div class=form-cnt>");
		out.print("<h3>Add Medical Record</h3>");
		out.print("<div class=form>");
		out.print("<input type=text name=medicalRecord>");
		out.print("<input type=date name=date>");
		out.print("<input type='submit' name='record' value='Send'>");
		out.print("</div>");
		out.print("</div>");
		out.print("</form>");
		out.print("</div>");
		out.print("</div>");}

}
