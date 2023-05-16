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
import helpers.MarketingHelper;
import helpers.User;
import helpers.UserHandler;
import server_conf.ThymeleafConfig;

/**
 * Servlet implementation class marketing
 */
@WebServlet("/MarketingServlet")
public class MarketingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MarketingServlet() {
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
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
	    if (user == null) {
	        response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
	        return;
	    }
	    System.err.println("marketing doget");
		FileHandler filehandler = new FileHandler(user);
		MarketingHelper marketing = new MarketingHelper(user);
		
		ArrayList<String> items = marketing.getValues();

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
		String values = request.getParameter("marketingText");
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
		System.err.println("marketing doPost");
	    if (user == null) {
	        response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
	        return;
	    }
		FileHandler filehandler = new FileHandler(user);
		MarketingHelper marketing = new MarketingHelper(user);
		marketing.addToMarketingFile(values);
		ArrayList<String> items = marketing.getValues();
		
		context.setVariable("items",items);
		
		String[] buttonVal = filehandler.getFileNames();

		context.setVariable("buttons",buttonVal);
		ThymeleafConfig.getTemplateEngine().process("marketing.html", context, response.getWriter());
	}

}