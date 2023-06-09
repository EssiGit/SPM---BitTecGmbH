package site_handling;



import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;

import user_handling.User;
import server_conf.ThymeleafConfig;
import org.apache.commons.lang3.time.StopWatch;
import org.thymeleaf.context.WebContext;

import file_handling.FileHandler;

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
		if(redirect(user,response)) {
			return;
		}

		FileHandler filehandler = new FileHandler(user);

		context.setVariable("buttons", filehandler.getFileNames());

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
		if(redirect(user,response)) {
			return;
		}


		FileHandler filehandler = new FileHandler(user);
		Part filePart;
		try {
		filePart = request.getPart("file-input");
		}catch(IllegalStateException e) {
			request.setAttribute("error", "Datei zu groß, upload abgebrochen!");
			this.doGet(request, response);
			return;
		}
		String fileName = filePart.getSubmittedFileName();
		processFileUpload(filehandler, fileName, request, response, session);



		watch.stop();
		System.out.println("time in main: " + watch.getTime() + " ms");
	}


	/**
	 * uploads file to File DIR
	 * @param fileHandler current users filehandler
	 * @param fileName
	 * @param request
	 * @param response
	 * @param session
	 */
	private void processFileUpload(FileHandler fileHandler, String fileName, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		try {
			if (fileHandler.uploadFile(fileName, request)) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WekaServlet");
				session.setAttribute("filename", fileName);

				dispatcher.forward(request, response);
			} else {
				request.setAttribute("error", "Datei enthält Fehler, upload abgebrochen!");
				this.doGet(request, response);
				fileHandler.deleteOldFile(fileName);
			}
		} catch (IOException | ServletException | JAXBException e) {
			request.setAttribute("error", "Datei enthält Fehler, upload abgebrochen!");
			e.printStackTrace();
		}
	}
	/**
	 * redirects to Index Servlet
	 * @param user
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public boolean redirect(User user,HttpServletResponse response) throws IOException {
		if (user == null) {
			response.sendRedirect("index"); 
			return true;
		}else {
			return false;
		}
	}
}
