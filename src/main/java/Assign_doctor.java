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

@WebServlet("/Assign_doctor")
public class Assign_doctor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String search = request.getParameter("search");
        String searchQuery = request.getParameter("search_query");
        String submit = request.getParameter("submit");

        try {
            out.print("<html>");
            out.print("<head>");
            out.print("<title>Assign Doctor</title>");
            out.print("<link rel=stylesheet type=text/css href=css/update.css>");
            out.print("<link rel=stylesheet type=text/css href=css/assign.css>");
            out.print("</head>");
            out.print("<body>");

            RequestDispatcher rd = request.getRequestDispatcher("header");
            rd.include(request, response);
	        out.print("<div class=back-search>");
            out.print("<a class=back href=PhysicianAss_home>Back</a>");
	        out.print("<div class=search>");
            out.print("<h2>Assign Doctor</h2>");
            out.print("<form class=Search-form action=Assign_doctor method=post>");
            out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
            out.print("<button class=search-btn name=search>Search</button>");
            out.print("</form>");
            out.print("</div>");
            out.print("</div>");

            
            if (search == null) {
                displayAllPatients(out);
            }

            if (search != null) {
                displaySearchResults(out, searchQuery);
            }

            if (submit != null) {
                assignDoctors(request, out);
            }

            out.print("</body>");
            out.print("</html>");
        } catch (ClassNotFoundException | SQLException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayAllPatients(PrintWriter out) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        Class.forName(driverName);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
            String sql = "SELECT * FROM patient_profile";
            String docSql = "SELECT * FROM user WHERE status='active' AND user_type = 'Doctor'";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                out.print("<Form action=Assign_doctor class=assign-wrapper>");
                out.print("<div class=table-active>");
                out.print("<table border=1>");
                out.print("<tr>");
                out.print("<th>Select</th>");
                out.print("<th>Patient Id</th>");
                out.print("<th>First Name</th>");
                out.print("<th>Second Name</th>");
                out.print("</tr>");
                while (rs.next()) {
                    out.print("<tr>");
                    out.print("<td><input type=checkbox name=selectedPatients value=" + rs.getString(1) + "></td>");
                    out.print("<td>" + rs.getString("patient_id") + "</td>");
                    out.print("<td>" + rs.getString("first_name") + "</td>");
                    out.print("<td>" + rs.getString("second_name") + "</td>");
                    out.print("</tr>");
                }
                out.print("</table>");

                try (PreparedStatement stmt1 = conn.prepareStatement(docSql);
                     ResultSet rs1 = stmt1.executeQuery()) {
                    out.print("<div class=active-doctors>");
                    out.print("<h3>Active Doctors</h3>");
                    boolean doctorsFound = false;
                    while (rs1.next()) {
                        doctorsFound = true;
                        out.print("<div class=doctor-list>");
                        out.print("<div class=doctor>");
                        out.print("<input type=checkbox name=selectedDoctors value=" + rs1.getString("user_id") + ">");
                        out.print("<span>" + rs1.getString("first_name") + " " + rs1.getString("last_name")
                                + "</span>");
                        out.print("</div>");
                        out.print("</div>");
                    }
                    if (!doctorsFound) {
                        out.print("<p>No active doctors found</p>");
                    }
                }

                out.print("</div>");
                out.print("</div>");

                out.print("<input class=assign-btn type=submit value=Assign name=submit>");
                out.print("</form>");
            }
        }
    }

    private void displaySearchResults(PrintWriter out, String searchQuery) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        Class.forName(driverName);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
            String sql = "SELECT * FROM patient_profile WHERE patient_id=? OR first_name LIKE ? OR second_name LIKE ?";
            String docSql = "SELECT * FROM user WHERE status='active' AND user_type = 'Doctor'";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, searchQuery); // Exact match for patient ID
                stmt.setString(2, "%" + searchQuery + "%"); // First Name (using LIKE for partial matches)
                stmt.setString(3, "%" + searchQuery + "%"); // Second Name (using LIKE for partial matches)
                ResultSet rs = stmt.executeQuery();

                out.print("<h3>Search Results</h3>");
                out.print("<div class=table-active>");
                out.print("<table border=1>");
                out.print("<tr>");
                out.print("<th>Select</th>");
                out.print("<th>Patient Id</th>");
                out.print("<th>First Name</th>");
                out.print("<th>Second Name</th>");
                out.print("</tr>");
                while (rs.next()) {
                    out.print("<tr>");
                    out.print("<td><input type=checkbox name=selectedPatients value=" + rs.getString(1) + "></td>");
                    out.print("<td>" + rs.getString("patient_id") + "</td>");
                    out.print("<td>" + rs.getString("first_name") + "</td>");
                    out.print("<td>" + rs.getString("second_name") + "</td>");
                    out.print("</tr>");
                }
                out.print("</table>");

                try (PreparedStatement stmt1 = conn.prepareStatement(docSql);
                     ResultSet rs1 = stmt1.executeQuery()) {
                    out.print("<div class=active-doctors>");
                    out.print("<h3>Active Doctors</h3>");
                    boolean doctorsFound = false;
                    while (rs1.next()) {
                        doctorsFound = true;
                        out.print("<div class=doctor-list>");
                        out.print("<div class=doctor>");
                        out.print("<input type=checkbox name=selectedDoctors value=" + rs1.getString("user_id") + ">");
                        out.print("<span>" + rs1.getString("first_name") + " " + rs1.getString("last_name")
                                + "</span>");
                        out.print("</div>");
                        out.print("</div>");
                    }
                    if (!doctorsFound) {
                        out.print("<p>No active doctors found</p>");
                    }
                }

                out.print("</div>");
                out.print("</div>");

                out.print("<input class=assign-btn type=submit value=Assign name=submit>");
                out.print("</form>");
            }
        }
    }

    private void assignDoctors(HttpServletRequest request, PrintWriter out) {
        String[] selectedPatients = request.getParameterValues("selectedPatients");
        String[] selectedDoctors = request.getParameterValues("selectedDoctors");

        if (selectedPatients == null || selectedPatients.length == 0 || selectedDoctors == null
                || selectedDoctors.length == 0) {
            out.println("<font color=red>Please select at least one patient and one doctor</font>");
            return;
        }

        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        try {
            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
                String assignSql = "INSERT INTO assigned_doctor(patient_id, doctor_id) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(assignSql)) {
                    for (String patientID : selectedPatients) {
                        for (String doctorID : selectedDoctors) {
                            pstmt.setString(1, patientID);
                            pstmt.setString(2, doctorID);
                            pstmt.addBatch();
                        }
                    }
                    int[] rowsAffected = pstmt.executeBatch();
                    int totalRowsAffected = 0;
                    for (int row : rowsAffected) {
                        totalRowsAffected += row;
                    }
                    if (totalRowsAffected > 0) {
                        out.println("<span>Assigned successfully</span>");
                    } else {
                        out.println("Failed to assign");
                    }
                } catch (SQLException e) {
                    out.println("Error: " + e.getMessage());
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
