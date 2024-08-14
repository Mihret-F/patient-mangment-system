import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/View_patient_profile")
public class View_patient_profile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public View_patient_profile() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String searchQuery = request.getParameter("search_query");
        String search = request.getParameter("search_query");

        out.print("<html><head>");
        out.print("<title>View Patient Profile</title></head>");
        out.print("<link rel=stylesheet type=text/css href=css/view.css>");
        out.print("<link rel=stylesheet type=text/css href=css/update.css>");
        
		out.print("</head>");
		out.print("<body>");

        RequestDispatcher rd = request.getRequestDispatcher("header");
        rd.include(request, response);
        
        out.print("<div class=back-search>");
        out.print("<a class='back' href='Reception_home'>Back</a>");
        out.print("<div class=search>");
        out.print("<form class=Search-form action=View_patient_profile method=post>"); 
        out.print("<input type=text name=search_query placeholder=PatientID/Name required>");
        out.print("<input class=search-btn type=submit value=Search>");
        out.print("</form>");
        out.print("</div>");
        out.print("</div>");

        String driverName = "com.mysql.cj.jdbc.Driver";
        String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
        String dbusername = "root";
        String dbpassword = "root";

        try {
            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
                if (search != null) {
                    String searchSql = "SELECT * FROM patient_profile WHERE patient_id=? OR first_name LIKE ? OR second_name LIKE ?";
                    try (PreparedStatement searchStmt = conn.prepareStatement(searchSql)) {
                        searchStmt.setString(1, searchQuery);
                        searchStmt.setString(2, "%" + searchQuery + "%"); 
                        searchStmt.setString(3, "%" + searchQuery + "%"); 

                        ResultSet rs = searchStmt.executeQuery();

                        displayUsers(out, rs);
                    }
                } else {
                    displayAllUsers(out, conn);
                }
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        } finally {
            out.println("</body></html>");
            out.close();
        }
    }

    private void displayAllUsers(PrintWriter out, Connection conn) throws Exception {
        
    	String sql = "SELECT * FROM patient_profile";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
           
        	 // Check if the result set is empty
            if (!rs.isBeforeFirst()) {
                out.println("<p>No patients found.</p>");
                return; // No need to display the table header if there are no patients
            }
        	
        	out.print("<div class=patient-container>");
            out.print("<div class=patient-left>");
            while (rs.next()) {
                out.print("<div class=patient-cards>");
                out.print("<div class=patient-card>");
                out.print("<img class=profile src=UploadedFiles/profile.jpg>");
                out.print("<div class=inf>");
                out.print("<span>ID: " + rs.getString("patient_id") + "</span>");
                out.print("<span>First Name: " + rs.getString("first_name") + "</span>");
                out.print("<span>Second Name: " + rs.getString("second_name") + "</span>");
                out.print("<span>Gender: " + rs.getString("gender") + "</span>");
                out.print("<span>Age: " + rs.getString("age") + "</span>");
                out.print("<span>Phone_No: " + rs.getString("phone_number") + "</span>");
                out.print("</div>");
                out.print("</div>");
                out.print("</div>");
            }
            out.print("</div>");
        }
    }

    private void displayUsers(PrintWriter out, ResultSet rs) throws Exception {
    	 
    	// Check if the result set is empty
        if (!rs.isBeforeFirst()) {
            out.println("<p>No patients found.</p>");
            return; // No need to display the table header if there are no patients
        }
        
    	out.print("<div class=patient-container>");
        out.print("<div class=patient-left>");
        while (rs.next()) {
            out.print("<div class=patient-card>");
            out.print("<img class=profile src=UploadedFiles/profile.jpg>");
            out.print("<div class=inf>");
            out.print("<span>ID: " + rs.getString("patient_id") + "</span>");
            out.print("<span>First Name: " + rs.getString("first_name") + "</span>");
            out.print("<span>Second Name: " + rs.getString("second_name") + "</span>");
            out.print("<span>Gender: " + rs.getString("gender") + "</span>");
            out.print("<span>Age: " + rs.getString("age") + "</span>");
            out.print("<span>Phone_No: " + rs.getString("phone_number") + "</span>");
            out.print("</div>");
            out.print("</div>");
        }
        out.print("</div>");
        out.print("</div>");
    }
}
