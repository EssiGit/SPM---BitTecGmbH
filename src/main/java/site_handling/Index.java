package site_handling;


import java.io.IOException;    
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import server_conf.ThymeleafConfig;
import org.thymeleaf.context.WebContext;

import file_handling.FileHandler;
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
	    try {
			handleLogin(request, response);
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, JAXBException {
	    String name = request.getParameter("nutzername");
	    String password = request.getParameter("password");
	    name = name.toLowerCase();

	    UserHandler userHand = new UserHandler();
	    if (userHand.checkForUser(name, password)) {
	        User user = new User(name);
	        
	        FileHandler filehandler = new FileHandler(user);
	        HttpSession session = request.getSession();
	        session.setAttribute("User", user);
	        
	        if(filehandler.setupForLogin()) {
		        String[] buttonVal = filehandler.getFileNames();
		        WebContext context = new WebContext(request, response, request.getServletContext());
		        context.setVariable("buttons", buttonVal);
		        response.sendRedirect("main");
	        	
	        }else {
	        	response.sendRedirect("index");	
	        }

	    } else {
	        WebContext context = new WebContext(request, response, request.getServletContext());
	        context.setVariable("error", "1");
	        ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());
	    }
	}

	

}