
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class header
 */
@WebServlet("/header")
public class header extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

		try {
			out.print("<html>");
			out.print("<head>");
			out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/header.css\">");
			out.print("</head>");
			out.print("<body");

			out.print("<div class=header-container>");
			
			out.print("<a class=logo href=Home>"
					+ "<div class=logoImg>"
					+ "<img src=UploadedFiles/download.png>"
					+ "</div>"
					+ "<div class=logoT>"
					+ "<h1>DM Refferal Hospital <br/>Patient Management System</h1>"
					+ "</div>"
					+ "</a>");
		
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");

		} catch (Exception e) {
			out.print(e.getMessage());
		}
	}

}