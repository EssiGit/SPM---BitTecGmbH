package site_handling;

import java.io.IOException;  
import helpers.MarketingHelper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import user_handling.PasswordHasher;
import org.thymeleaf.context.WebContext;

import helpers.FileHandler;
import user_handling.SetupUser;
import user_handling.User;
import server_conf.ThymeleafConfig;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext context = new WebContext(request, response,
                request.getServletContext());
        response.setCharacterEncoding("UTF-8");
        ThymeleafConfig.getTemplateEngine().process("register.html", context, response.getWriter());

		System.out.println("testing post test");
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setCharacterEncoding("UTF-8");
    request.setCharacterEncoding("UTF-8");
    WebContext context = new WebContext(request, response,
            request.getServletContext());
	String name = request.getParameter("username");
	String password = request.getParameter("password");
	System.out.println("pw " + password);
	name = name.toLowerCase();
	PasswordHasher hasher = new PasswordHasher();
	SetupUser setup = new SetupUser(name,hasher.hashPassword(password));
	if(setup.errorMsg().equals("none") && !password.equals("")) { //bad overhead, ugly too
		
		User user = new User(name);
		FileHandler filehandler = new FileHandler(user);
		setup.addUser(filehandler);
		MarketingHelper marketing = new MarketingHelper(user);
		marketing.newMarketingFile();

	    response.sendRedirect("index");
	}else {
		System.out.println(setup.errorMsg());
		context.setVariable("error", "1");
		context.setVariable("errorMsg", setup.errorMsg());
		ThymeleafConfig.getTemplateEngine().process("register.html", context, response.getWriter());
		
	}

	
}

}