package site_handling;


import java.io.IOException;  
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import server_conf.ThymeleafConfig;
import org.thymeleaf.context.WebContext;

import helpers.FileHandler;
import helpers.User;
import helpers.UserHandler;


@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		System.out.println("in doget");
		ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		HttpSession session = request.getSession();
		System.out.println("in doPost");
		String name = request.getParameter("nutzername");
		System.out.println("Index Name: " + name);
		if(name.isEmpty()) {name = " ";};
		name = name.toLowerCase();
		UserHandler userHand = new UserHandler();
		

		if(userHand.checkForUserName(name)) {
			User user = new User(name);
			FileHandler filehandler = new FileHandler(user);
			session.setAttribute("User", user);

			request.setAttribute("User", user);

			String[] buttonVal = filehandler.getFileNames();
				context.setVariable("buttons",buttonVal);
			response.sendRedirect("main");

		}else {
			
			context.setVariable("error", "1");
			ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());
		
		}
	}

}