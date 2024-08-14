

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LabTech_home")
public class LabTech_home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);

        HttpSession session = request.getSession(true);

        // Check if user is logged in
        if (session.getAttribute("uname") == null || session.getAttribute("utype") == null) {
            response.sendRedirect("Home");
            return;
        }

        String utype = (String) session.getAttribute("utype");
        String uname = (String) session.getAttribute("uname");

        // Check if user is not of type "Physician Assistant"
        if (!utype.equals("Lab_Technician")) {
            response.sendRedirect("Home"); // Redirect to Home page
            return;
        }

        PrintWriter out = response.getWriter();
        try {
			out.print("<html>");
			out.print("<head>");
			out.print("<title>Lab Technician Home Page</title>");
			out.print("<link rel=stylesheet type=text/css href=css/reception_home.css>");
			out.print("</head>");
			out.print("<body>");

			RequestDispatcher rd = request.getRequestDispatcher("header");
			rd.include(request, response);

			out.print("<div class=recep-cntr>");
			out.print("<h2> " + utype + " Home Page </h2>");
			out.print("<span class=welcome> Welcome " + uname
					+ " <a class=logout href=Logout><input type=submit value=Logout></a></span>");
			out.print("<div class=recep-actions>");
			out.print("<a href=Test_result>ViewTestRequest </a>");
			out.print("</div>");
			out.print("</div>");

			out.print("</body>");
			out.print("</html>");
		} catch (Exception e) {
			out.print(e.getMessage());
		}
	
	}

}