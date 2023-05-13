package site_handling;

import helpers.FileHandler;
import helpers.User;
import helpers.UserHandler;
import server_conf.ThymeleafConfig;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(request, response,
                request.getServletContext());
		HttpSession session = request.getSession();
		String name = request.getParameter("nutzername");
		System.out.println("Index Name: " + name);
		if(name.isEmpty()) {name = "default";};
		User user = new User(name);
		UserHandler userHand = new UserHandler();
		FileHandler filehandler = new FileHandler(user);
		 
		if(userHand.checkForUserName(name)==false) {
		session.setAttribute("User", user);
		
		request.setAttribute("User", user);

       
       
        String[] buttonVal = filehandler.getFileNames();
        for(int i=1;i<=5;i++) {
        	context.setVariable("button"+i,buttonVal[i-1]);
        }
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());

		}else {
			ThymeleafConfig.getTemplateEngine().process("index.html", context, response.getWriter());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
