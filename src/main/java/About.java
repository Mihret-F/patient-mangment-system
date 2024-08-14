
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class About
 */
@WebServlet("/About")
public class About extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public About() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

		try {
			out.print("<html>");
			out.print("<head>");
			out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/about.css\">");
			out.print("</head>");
			out.print("<body");
			RequestDispatcher rd = request.getRequestDispatcher("header");
			rd.include(request, response);
			out.print("<div class=about-wrapper>");
			out.print("<h1>About Us</h1>");
			out.print("<div class=about-container>");
			
			out.print("<div class=about-left>");
			out.print("<img src=UploadedFiles/pexels-pixabay-269077.jpg>");
			out.print("</div>");
			out.print("<div class=about-right><h3>Empowering Healthcare Excellence</h3>\r\n" + ""
					+ "At Dm Refferal Hospital, we believe that efficient patient management is the cornerstone of exceptional healthcare delivery."+
					"Our mission is to streamline administrative tasks, enhance patient experiences, and enable healthcare professionals to focus on what truly matters: compassionate care."
					+"<h3>What We Offer</h3>"
					+"<ul>"
					+ "<li>Comprehensive Patient Records</li> Our robust system captures and organizes essential patient information, including medical history, treatments, medications, and laboratory results. Say goodbye to manual record-keeping and hello to digital efficiency."
					+ "<li>Referral Management</li> Bridge gaps between departments and locations by facilitating seamless information sharing. Collaborate effortlessly with specialists, ensuring continuity of care.\r\n"
					+"</ul>");
			out.print("</div>");
			out.print("</div>");
			out.print("</body>");
			out.print("</html>");

		} catch (Exception e) {
			out.print(e.getMessage());
		}
	}

}
