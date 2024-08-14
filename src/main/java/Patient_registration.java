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

@WebServlet("/Patient_registration")
public class Patient_registration extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String submit = request.getParameter("submit");

        try {
            out.print("<html>");
            out.print("<head>");
            out.print("<title>Reception Home Page</title>");
            out.print("<link rel=stylesheet type=text/css href=css/patient_reg.css>");
            out.print("</head>");
            out.print("<body>");
            out.print("<script src='javascript/reset.js'></script>");


            RequestDispatcher rd = request.getRequestDispatcher("header");
            rd.include(request, response);
            out.print("<div class=reg-wrapper>");
            out.print("<a class=back href=Reception_home>Back</a>");

            out.print("<form class=registration-form action=Patient_registration method=post>");
            out.print("<h2>Registration</h2>");
            out.print("<input type=text name=patient_id placeholder='PatientID' required>");
            out.print("<input type=text name=first_name placeholder='First Name' required>");
            out.print("<input type=text name=second_name placeholder='Second Name' required>");
            out.print("<input type=text name=age placeholder=Age required>");
            out.print("<input type=text name=gender placeholder=Gender required>");
            out.print("<input type=text name=phone_number placeholder=Phone Number required>");

            out.print("<div class=reg-btns>");
            out.print("<input class=reg-btn type=submit name=submit placeholder=Register>");
            out.print("<button class=reset-btn onclick=\"refreshRegForm()\">Reset</button>");
            out.print("</div>");
            out.print("</form>");
            out.print("</div>");

            out.print("</body>");
            out.print("</html>");

            // Handle form submission
            if (submit != null) {
                String driverName = "com.mysql.cj.jdbc.Driver";
                String dbUrl = "jdbc:mysql://localhost:3306/patient_management_system";
                String dbUsername = "root";
                String dbPassword = "root";

                try {
                    Class.forName(driverName);
                    try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                        String patientID = request.getParameter("patient_id");

                        // Check if the patient ID already exists
                        String selectSql = "SELECT * FROM patient_profile WHERE patient_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                            stmt.setString(1, patientID);
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    out.print("Patient ID already exists");
                                } else {
                                    String firstName = request.getParameter("first_name");
                                    String secondName = request.getParameter("second_name");
                                    String ageStr = request.getParameter("age");
                                    String gender = request.getParameter("gender");
                                    String phoneNumber = request.getParameter("phone_number");

                                    // Convert age to integer
                                    int age = Integer.parseInt(ageStr);

                                    // Insert the new patient record
                                    String sql = "INSERT INTO patient_profile (patient_id, first_name, second_name, age, gender, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
                                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                        pstmt.setString(1, patientID);
                                        pstmt.setString(2, firstName);
                                        pstmt.setString(3, secondName);
                                        pstmt.setInt(4, age);
                                        pstmt.setString(5, gender); 
                                        pstmt.setString(6, phoneNumber);

                                        int rowsAffected = pstmt.executeUpdate();

                                        if (rowsAffected > 0) {
                                            out.println("<span>Patient created successfully</span>");
                                        } else {
                                            out.println("Failed to insert the data");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException | SQLException e) {
                    out.println(e.getMessage());
                }
            }
        } finally {
            out.close();
        }
    }
}
