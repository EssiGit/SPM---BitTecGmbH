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

import file_handling.FileHandler;
import marketing.MarketingHelper;
import user_handling.User;
import server_conf.ThymeleafConfig;

@WebServlet("/MarketingServlet")
public class MarketingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		WebContext context = new WebContext(request, response, request.getServletContext());

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("User");
		if (redirect(user, response)) {
			return;
		}

		FileHandler fileHandler = new FileHandler(user);
		MarketingHelper marketingHelper = new MarketingHelper(user);

		ArrayList<String> items;


		items = marketingHelper.getValues();
		context.setVariable("items", items);



		context.setVariable("buttons", fileHandler.getFileNames());

		ThymeleafConfig.getTemplateEngine().process("marketing.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		String values = request.getParameter("marketingText");
		WebContext context = new WebContext(request, response, request.getServletContext());
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("User");


		if (redirect(user, response)) {
			return;
		}

		FileHandler fileHandler = new FileHandler(user);
		MarketingHelper marketingHelper = new MarketingHelper(user);

		marketingHelper.addToMarketingFile(values);

		ArrayList<String> items = marketingHelper.getValues();
		context.setVariable("items", items);




		context.setVariable("buttons", fileHandler.getFileNames());

		ThymeleafConfig.getTemplateEngine().process("marketing.html", context, response.getWriter());
	}

	public boolean redirect(User user, HttpServletResponse response) throws IOException {
		if (user == null) {
			response.sendRedirect("index");
			return true;
		} else {
			return false;
		}
	}


}

