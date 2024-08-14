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

@WebServlet("/Update_patient_profile")
public class Update_patient_profile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        String action = request.getParameter("action");
        String patientID = request.getParameter("patient_id");
        String firstName = request.getParameter("first_name");
        String secondName = request.getParameter("second_name");
        String ageStr = request.getParameter("age");
        String gender = request.getParameter("gender");
        String phoneNumber = request.getParameter("phone_number");
        String searchQuery = request.getParameter("search_query");


        try {
        	out.print("<html>");
    		out.print("<head>");
    		out.print("<title> Update Patient Profile</title>"); 
			out.print("<link rel=stylesheet type=text/css href=css/update.css>");
			out.print("<link rel=stylesheet type=text/css href=css/patient_reg.css>");
			out.print("</head>");
    		out.print("<body>");
            out.print("<script src='javascript/reset.js'></script>");

			RequestDispatcher rd = request.getRequestDispatcher("header");
			rd.include(request, response);
	        out.print("<div class=back-search>");
            out.print("<a class=back href=Reception_home>Back</a>");
	        out.print("<div class=search>");
    		out.print("<h2> Patient Profile Update Form </h2>");
    		out.print("<form class=Search-form action=Update_patient_profile method=post>");
    		out.print("<input type=text name=search_query placeholder=PatientID required>");
    		out.print("<input type=hidden name=action value=search>"); // To identify search action
            out.print("<input class=search-btn type=submit value=Search>");
    		out.print("</form>");
            out.print("</div>");
            out.print("</div>");
    		


    		
            String driverName = "com.mysql.cj.jdbc.Driver";
            String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
            String dbusername = "root";
            String dbpassword = "root";

            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(dbUrl, dbusername, dbpassword)) {
            	if ("search".equals(action)) {
            	    String searchSql = "SELECT * FROM patient_profile WHERE patient_id=?";
            	    try (PreparedStatement searchStmt = conn.prepareStatement(searchSql)) {
            	    	 // Set the search criteria based on the provided search_query
            	        searchStmt.setString(1, searchQuery); // Exact match for patient ID
            	        ResultSet rs = searchStmt.executeQuery();

                        if (!rs.next()) {
                            out.print("<font color=red>No patient found with provided information</font>");
                        } else {
                            out.print("<div class=reg-wrapper>");
                            out.print("<form class=registration-form action=Update_patient_profile method=post>");
                            out.print("<h2>Update</h2>");
                            out.print("<input type=text name=patient_id value=" + rs.getString("patient_id") + " required readonly>");
                            out.print("<input type=text name=first_name value=" + rs.getString("first_name") + " required>");
                            out.print("<input type=text name=second_name value=" + rs.getString("second_name") + " required>");
                            out.print("<input type=text name=age value=" + rs.getInt("age") + " required>");
                            out.print("<input type=text name=gender value=" + rs.getString("gender") + " required>");
                            out.print("<input type=text name=phone_number value=" + rs.getInt("phone_number") + " required>");
                            out.print("<div class=reg-btns>");
                            out.print("<input type=hidden name=action value=update>"); // To identify update action
                            out.print("<input class=reg-btn type=submit name=update value=Update>");
                            out.print("<button class=reset-btn onclick=\"refreshUpdateForm()\">Reset</button>");
                            out.print("</div>");
                            out.print("</form>");
                            out.print("</div>");
                        }
                    }
                } else if ("update".equals(action)) {
                    int age = Integer.parseInt(ageStr);

                    String updateSql = "UPDATE patient_profile SET first_name=?, second_name=?, age=?, gender=?, phone_number=? WHERE patient_id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, firstName);
                        updateStmt.setString(2, secondName);
                        updateStmt.setInt(3, age);
                        updateStmt.setString(4, gender);
                        updateStmt.setString(5, phoneNumber);
                        updateStmt.setString(6, patientID);
                        int rowsUpdated = updateStmt.executeUpdate();

                        if (rowsUpdated > 0) {
                            out.print("<font color=green>Profile updated successfully</font>");
                        } else {
                            out.print("<font color=red>Failed to update profile</font>");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            out.println("Error: " + e.getMessage());
        } finally {
            out.close();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    // Forward to doPost method to handle GET requests
    doPost(request, response);
}
}
