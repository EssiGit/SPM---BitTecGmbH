package site_handling;

import java.io.IOException;
import helpers.MarketingHelper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;

import helpers.FileHandler;
import helpers.SetupUser;
import helpers.User;
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
	System.out.println("Index Name: " + name);
	System.out.println("stil in POST");
	if(name.isEmpty() == false) {
	name = name.toLowerCase();
	User user = new User(name);
	SetupUser setup = new SetupUser(user);
	if(setup.checkIfexists()==false) {
		setup.addUser();
		MarketingHelper marketing = new MarketingHelper(user);
		marketing.newMarketingFile();
	    FileHandler filehandler = new FileHandler(user);
	    String[] buttonVal = filehandler.getFileNames();
	    for(int i=1;i<=5;i++) {
	    	context.setVariable("button"+i,buttonVal[i-1]);
	    }
	    response.sendRedirect("index");
	}else {
		context.setVariable("error", "1");
		ThymeleafConfig.getTemplateEngine().process("register.html", context, response.getWriter());
		
	}

	}
	
}

}