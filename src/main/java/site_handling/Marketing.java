package site_handling;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;

import helpers.FileHandler;
import helpers.User;
import helpers.UserHandler;
import server_conf.ThymeleafConfig;

/**
 * Servlet implementation class marketing
 */
@WebServlet("/MarketingServlet")
public class Marketing extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Marketing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
	    if (user == null) {
	        response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
	        return;
	    }
		System.out.println("TEST " + user.getName());
		FileHandler filehandler = new FileHandler(user);
		ArrayList<String> items = new ArrayList<>();
		
	
		items.add("Gestaltung ansprechender Schaufenster, um das Interesse von\r\n"
				+ "		Passanten zu wecken und neue Kunden zu gewinnen.");
		items.add("Durchführung von Rabattaktionen, Verlosungen oder anderen\r\n"
				+ "		Promotionen, um Kunden anzulocken und die Umsätze zu steigern.");
		items.add("Angebot von kostenlosen Produktproben, um Kunden von neuen\r\n"
				+ "		Produkten zu überzeugen und die Markentreue zu stärken.");
		items.add("Durchführung von Kundenumfragen, um Feedback zu erhalten\r\n"
				+ "		und das Angebot an Produkten und Dienstleistungen zu optimieren.");
		items.add("Organisation von Events wie Verkostungen oder Workshops, um\r\n"
				+ "		Kunden in den Laden zu locken und das Image des Ladens zu stärken.");
		items.add("Einrichtung eines Treueprogramms, um bestehende Kunden an\r\n"
				+ "		den Laden zu binden und die Wiederholungskäufe zu steigern.");
		
		context.setVariable("items",items);
		
		String[] buttonVal = filehandler.getFileNames();

		context.setVariable("buttons",buttonVal);
		ThymeleafConfig.getTemplateEngine().process("marketing.html", context, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
