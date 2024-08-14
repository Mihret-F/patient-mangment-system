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

@WebServlet("/View_medicalRecord")
public class View_medicalRecord extends HttpServlet {
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
        out.print("<a class='back' href='Add_medicalRecord'>Back</a>");
        out.print("<div class=search>");
        out.print("<form class=Search-form action='' method=get>"); 
        out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
        out.print("<button name=search class=search-btn >Search</button>");
        out.print("</form>");
        out.print("</div>");
        out.print("</div>");
        

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("user_id");

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

    

    private void displaySearchResults(PrintWriter out, String searchQuery, String userId) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        Class.forName(driverName);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {

//            String sql = "SELECT * FROM patient_profile pp JOIN patient_history ad ON pp.patient_id = ad.patient_id WHERE (pp.patient_id=? OR pp.first_name LIKE ? OR pp.second_name LIKE ?)";
        	 String sql = "SELECT *"+
        	"FROM patient_profile pp"+
        	"JOIN patient_history ad ON pp.patient_id = ad.patient_id"+
        	"JOIN assigned_doctor adoc ON pp.patient_id = adoc.patient_id"+
        	"WHERE (pp.patient_id = ? OR pp.first_name LIKE ? OR pp.second_name LIKE ?)"+
        	 "AND adoc.doctor_id = ?";

        	try (PreparedStatement searchStmt = conn.prepareStatement(sql)) {
                searchStmt.setString(1, searchQuery);
                searchStmt.setString(2, "%" + searchQuery + "%");
                searchStmt.setString(3, "%" + searchQuery + "%");
                searchStmt.setString(4, userId);

                try (ResultSet rs = searchStmt.executeQuery()) {
    				displayPatientsTable(out, rs);
                }
            }
        }
    }

    private void displayAllPatients(PrintWriter out, String userId) throws ClassNotFoundException, SQLException {
//        String sql = "SELECT * FROM patient_profile pp JOIN patient_history ad ON pp.patient_id = ad.patient_id";
    	String sql = "SELECT * FROM patient_profile pp " +
                "JOIN patient_history ad ON pp.patient_id = ad.patient_id " +
                "JOIN assigned_doctor adoc ON pp.patient_id = adoc.patient_id " +
                "WHERE adoc.doctor_id = ?";


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_management_system",
                    "root", "root");
            		  PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
    				displayPatientsTable(out, rs);
                }
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
    	out.print("<dic class=test-form>");
        out.print("<div class=assigned_patients>");
        out.print("<h2>Medical Record</h2>");
        out.print("<table border=1>");
        
        out.print("<tr>");
        out.print("<th>Patient Id</th>");
        out.print("<th>Date</th>");
        out.print("<th>Medical Record</th>");
        out.print("</tr>");
        while (rs.next()) {
            out.print("<tr>");
            out.print("<td>" + rs.getString("patient_id") + "</td>");
            out.print("<td>" + rs.getString("date") + "</td>");
            out.print("<td>" + rs.getString("medical_record") + "</td>");
            out.print("</tr>");
        }
        out.print("</table>");

        out.print("</div>");
        out.print("</div>");     
        out.print("</div>");
	}
}
