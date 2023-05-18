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
import user_handling.UserHandler;
import user_handling.User;


@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		HttpSession session = request.getSession();
		String name = request.getParameter("nutzername");
		System.out.println("Index Name: " + name);

		String password = request.getParameter("password");


		name = name.toLowerCase();
		UserHandler userHand = new UserHandler();


		if(userHand.checkForUser(name, password)) {
			User user = new User(name);
			FileHandler filehandler = new FileHandler(user);
			session.setAttribute("User", user);

			String[] buttonVal = filehandler.getFileNames();
			context.setVariable("buttons",buttonVal);
			response.sendRedirect("main");

		}else {

			context.setVariable("error", "1");
			ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());

		}
	}

}