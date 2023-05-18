package site_handling;


import java.io.File;   
import java.io.IOException;
import helpers.CSVCheck;
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
import user_handling.UserHandler;
import user_handling.User;
import server_conf.ThymeleafConfig;
import org.apache.commons.lang3.time.StopWatch;
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
		if (user == null) {
			response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
			return;
		}
		UserHandler userHand = new UserHandler();
		System.out.println("TEST " + user.getName());
		FileHandler filehandler = new FileHandler(user);

		String[] buttonVal = filehandler.getFileNames();

		context.setVariable("buttons",buttonVal);
		System.out.println("main do get");
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StopWatch watch = new StopWatch();
		watch.start();
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("main do post");
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("User");
		if (user == null) {
			response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
			return;
		}
		FileHandler filehandler = new FileHandler(user);
		System.out.println(request.getContentType());
		System.out.println(request.getParameter("file-input"));

		Part filePart = request.getPart("file-input");

		System.out.println(filePart.getSize());

		String fileName = filePart.getSubmittedFileName();


		File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + fileName);


		//TMP solution:
		System.out.println("filename in doPost " + fileName);
		filehandler.setUpFILE(DIR, request);
		CSVCheck csvchecker = new CSVCheck();
		boolean csv = csvchecker.checkCSV(DIR.getAbsolutePath());
		System.out.println("csv bool false = error" + csv);
		String buttonValue = request.getParameter("selectedButton");
		System.out.println("context: " + buttonValue);
		session.setAttribute("filename", fileName);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WekaServlet");
		watch.stop();
		System.out.println("main setup : " + watch.getTime() + " ms");
		watch.reset();
		watch.start();
		if(csvchecker.checkCSV(DIR.getAbsolutePath())){
			watch.stop();
			System.out.println("time: " + watch.getTime() +" ms");
			dispatcher.forward(request, response);
		}else {
			System.out.println("time: " + watch.getTime() +" ms");
			watch.stop();
			request.setAttribute("error", ".csv file format is not correct, upload canceled!");
			this.doGet(request, response);
			filehandler.deleteOldFile(fileName);
		}
	}
}
