import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/check")
public class check extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        // Establish database connection
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbUsername = "root";
        String dbPassword = "root";

        try {
            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                if (action != null && action.equals("delete")) {
                    String patientId = request.getParameter("patientId");
                    if (patientId != null) {
                        // Delete patient from the database
                        String deleteSql = "DELETE FROM patient_profile WHERE patient_id = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setString(1, patientId);
                            deleteStmt.executeUpdate();
                        }
                        // Redirect to refresh the page
                        response.sendRedirect(request.getRequestURI());
                        return;
                    }
                }

                // Display assigned patients
                String selectSql = "SELECT * FROM patient_profile";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                     ResultSet rs = selectStmt.executeQuery()) {
                    out.println("<html><head><title>View Assigned Patients</title></head><body>");
                    out.println("<h2>Assigned Patients</h2>");
                    out.println("<table border='1'>");
                    out.println("<tr><th>Patient ID</th><th>First Name</th><th>Last Name</th><th>Action</th></tr>");
                    while (rs.next()) {
                        String patientId = rs.getString("patient_id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        out.println("<tr>");
                        out.println("<td>" + patientId + "</td>");
                        out.println("<td>" + firstName + "</td>");
                        out.println("<td>" + lastName + "</td>");
                        out.println("<td><form method='post' action='View_assignedPatients'><input type='hidden' name='action' value='delete'><input type='hidden' name='patientId' value='" + patientId + "'><input type='submit' value='Delete'></form></td>");
                        out.println("</tr>");
                    }
                    out.println("</table></body></html>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("Error: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        service(request, response);
    }
}
