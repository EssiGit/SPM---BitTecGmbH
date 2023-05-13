package site_handling;


import java.io.File; 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import helpers.FileHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import helpers.User;
import helpers.UserHandler;
import server_conf.ThymeleafConfig;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

@WebServlet("/main")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		maxFileSize = 1024 * 1024 * 10,      // 10 MB
		maxRequestSize = 1024 * 1024 * 100   // 100 MB
		)
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
		UserHandler userHand = new UserHandler();
		System.out.println("TEST " + user.getName());
		FileHandler filehandler = new FileHandler(user);

		String[] buttonVal = filehandler.getFileNames();
		/* for(int i=1;i<=5;i++) {
        	context.setVariable("button"+i,buttonVal[i-1]);
        }*/
		context.setVariable("buttons",buttonVal);
		System.out.println("main do get");
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("main do post");
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
		UserHandler userHand = new UserHandler();
		FileHandler filehandler = new FileHandler(user);

		Iterator<String> paras = request.getParameterNames().asIterator();
		System.out.println(request.getContentType());
		System.out.println(request.getParameter("file-input"));
		while(paras.hasNext())
		{
			System.out.println(paras.next());
		}

		Part filePart = request.getPart("file-input");

		System.out.println(filePart.getSize());

		String fileName = filePart.getSubmittedFileName();


		File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + fileName);

		//TMP solution:

		filehandler.setUpFILE(DIR, request);
		String buttonValue = request.getParameter("selectedButton");
		System.out.println("context: " + buttonValue);
		session.setAttribute("selButton", fileName);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WekaServlet");
		dispatcher.forward(request, response);
	}

	/*<form name="button1" method="post" action="WekaServlet">
						<span th:unless="${button1 == 'Empty'}"> <input
							type="hidden" name="button1" th:value="${button1}">
						</span>
						<button type="submit" th:text="${button1}"></button>
					</form>

					<form name="button2" method="post" action="WekaServlet">
						<span th:unless="${button2 == 'Empty'}"> <input
							type="hidden" name="button2" th:value="${button2}">
						</span>
						<button type="submit" th:text="${button2}"></button>
					</form>

					<form name="button3" method="post" action="WekaServlet">
						<span th:unless="${button3 == 'Empty'}"> <input
							type="hidden" name="button3" th:value="${button3}">
						</span>
						<button type="submit" th:text="${button3}"></button>
					</form>

					<form name="button4" method="post" action="WekaServlet">
						<span th:unless="${button4 == 'Empty'}"> <input
							type="hidden" name="button4" th:value="${button4}">
						</span>
						<button type="submit" th:text="${button4}"></button>
					</form>



					<span th:unless="${button4 == 'Empty'}">
						<form name="button5" method="post" action="WekaServlet">
					</span> <span th:if="${button4 == 'Empty'}">
						<form name="button5" method="post">
					</span> <input type="hidden" name="button5" th:value="${button5}">

					<button type="submit" th:text="${button5}"></button>
					</form>*/

}
