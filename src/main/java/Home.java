import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Expires", "0");
		response.setDateHeader("Expires", -1);
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			out.print("<html>");
			out.print("<head>");
			out.print("<title>Home Page</title>");
			out.print("<link rel=stylesheet type=text/css href=css/home.css>");
			out.print("</head>");
			out.print("<body class=body>");
			RequestDispatcher rd = request.getRequestDispatcher("header");
			rd.include(request, response);
            out.print("<script src='javascript/reset.js'></script>");

			out.print("<div class=home-wrapper>");

			out.print("<div class=head>");
			out.print("<h1>Welcome to Patient Management System</h1>");
			out.print("<a href=About>About</a>");
			out.print("</div>");

			out.print("<form class=login-form action=ValidateUser method=post autocomplete=off>");
			out.print("<h2>Login</h2>");
			out.print("<input type=text name=username placeholder=Username required>");
			out.print("<input type=password name=password placeholder=Password required>");
			out.print("<div class=login-btns>");
			out.print("<button class=login-btn name=submit/>Login</button>");
            out.print("<button class=reset-btn onclick=\"refreshLogInForm()\">Reset</button>");
			out.print("</div>");
			out.print("</form>");

			out.print("</div>");

			out.print("</body>");
			out.print("</html>");

		} catch (Exception e) {
			out.print(e.getMessage());
		}
	}
}