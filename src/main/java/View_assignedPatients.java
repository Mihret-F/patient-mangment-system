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

@WebServlet("/View_assignedPatients")
public class View_assignedPatients extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String search = request.getParameter("search");
        String searchQuery = request.getParameter("search_query");
        String selectedTest = request.getParameter("selected_test");
        String send = request.getParameter("send");
        String selectedPatient = request.getParameter("selectedPatient");
        String deletePatientId = request.getParameter("delete");

        out.print("<html>");
        out.print("<head>");
        out.print("<title>View Assigned Patients</title>");
        out.print("<link rel=stylesheet type=text/css href=css/update.css>");
        out.print("<link rel=stylesheet type=text/css href=css/assign.css>");
        out.print("</head>");
        out.print("<body>");

        RequestDispatcher rd = request.getRequestDispatcher("header");
        rd.include(request, response);

        // Display search form
        out.print("<div class=back-search>");
        out.print("<a class='back' href='Doctor_home'>Back</a>");
        out.print("<div class=search>");
        out.print("<form class=Search-form action=View_assignedPatients method=post>");
        out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
        out.print("<button name=search class=search-btn> Search</button>");
        out.print("</form>");
        out.print("</div>");
        out.print("</div>");

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("user_id");

        // Display all patients initially
        if (search == null) {
            try {
                displayAllPatients(out, userId);
            } catch (ClassNotFoundException | SQLException e) {
                out.println("Error: " + e.getMessage());
            }
        }

        // Display searched results
        if (search != null) {
            try {
                displaySearchResults(out, searchQuery, userId);
            } catch (ClassNotFoundException | SQLException e) {
                out.println("Error: " + e.getMessage());
            }
        }

        if (send != null) {
            if (selectedPatient == null || selectedTest == null) {
                out.print("<script>alert('Error: missing data.');</script>");
                return;
            }
            sendTestRequest(out, selectedPatient, selectedTest);
        }

        if (deletePatientId != null) {
            deletePatient(deletePatientId);
            response.sendRedirect(request.getContextPath() + "/View_assignedPatients");
        }

        out.print("</body>");
        out.print("</html>");
    }

    private void displayAllPatients(PrintWriter out, String doctorId) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        Class.forName(driverName);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
            String sql = "SELECT * FROM patient_profile pp JOIN assigned_doctor ad ON pp.patient_id = ad.patient_id WHERE ad.doctor_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    displayPatientsTable(out, rs);
                }
            }
        }
    }

    private void displaySearchResults(PrintWriter out, String searchQuery, String doctorId) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        Class.forName(driverName);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
            String sql = "SELECT * FROM patient_profile pp JOIN assigned_doctor ad ON pp.patient_id = ad.patient_id WHERE ad.doctor_id = ? AND (pp.patient_id=? OR pp.first_name LIKE ? OR pp.second_name LIKE ?)";
            try (PreparedStatement searchStmt = conn.prepareStatement(sql)) {
                searchStmt.setString(1, doctorId);
                searchStmt.setString(2, searchQuery);
                searchStmt.setString(3, "%" + searchQuery + "%");
                searchStmt.setString(4, "%" + searchQuery + "%");
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
        out.print("<form class=test-form action=View_assignedPatients method=post>");
        out.print("<div class=assigned_patients>");
        out.print("<h2>Assigned Patients</h2>");
        out.print("<table border=1>");
        out.print("<tr>");
        out.print("<th>Select</th>");
        out.print("<th>Patient Id</th>");
        out.print("<th>First Name</th>");
        out.print("<th>Last Name</th>");
        out.print("<th>Action</th>");
        out.print("</tr>");
        while (rs.next()) {
            out.print("<tr>");
            out.print("<td><input type=radio name=selectedPatient value=" + rs.getString(1) + "></td>");
            out.print("<td>" + rs.getString("patient_id") + "</td>");
            out.print("<td>" + rs.getString("first_name") + "</td>");
            out.print("<td>" + rs.getString("second_name") + "</td>");
            out.print("<td><button type='submit' name='delete' value='" + rs.getString(1) + "'>Delete</button></td>");
            out.print("</tr>");
        }
        out.print("</table>");
        out.print("<div class=form-cnt>");
        out.print("<h3>Laboratory Test Types</h3>");
        out.print("<div class=form>");

        out.print("<input type='radio' name='selected_test'> Blood Test");
        out.print("<input type='radio' name='selected_test'> Urine Test");
        out.print("<input type='radio' name='selected_test'> X_Ray");
        out.print("<input type='radio' name='selected_test'> MRI");
        out.print("<input class=send type='submit' name='send' value='Send'>");
        out.print("</div>");
        out.print("</div>");
        out.print("</form>");
        out.print("</div>");
        out.print("</div>");
    }

    private void deletePatient(String patientId) {
        String deleteQuery = "DELETE FROM assigned_doctor WHERE patient_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                    "root", "root"); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, patientId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Patient deleted successfully.");
                } else {
                    System.out.println("Failed to delete patient.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendTestRequest(PrintWriter out, String selectedPatient, String selectedTest) {
        String insertQuery = "INSERT INTO laboratory_test (patient_id, test_request) " + "SELECT pp.patient_id, ? "
                + "FROM patient_profile pp " + "WHERE pp.patient_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                    "root", "root"); PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, selectedTest);
                stmt.setString(2, selectedPatient);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    out.print("<script>alert('Test request sent successfully.');</script>");
                } else {
                    out.print("<script>alert('Failed to sent test request.');</script>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            out.print("<script>alert('Error: " + e.getMessage() + "');</script>");
            e.printStackTrace();
        }
    }
}
