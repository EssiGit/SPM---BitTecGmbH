package site_handling;


   
import java.io.IOException;
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
		if(redirect(user,response)) {
			return;
		}

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
		if(redirect(user,response)) {
			return;
		}


		FileHandler filehandler = new FileHandler(user);

		Part filePart = request.getPart("file-input");

		String fileName = filePart.getSubmittedFileName();
	    processFileUpload(filehandler, fileName, request, response, session);
	    watch.stop();
        System.out.println("time in main: " + watch.getTime() + " ms");
	}
	
	
	private void processFileUpload(FileHandler fileHandler, String fileName, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
	    if (fileHandler.uploadFile(fileName, request)) {
	        RequestDispatcher dispatcher = request.getRequestDispatcher("/WekaServlet");
	        session.setAttribute("filename", fileName);
	        
	        dispatcher.forward(request, response);
	    } else {
	    	request.setAttribute("error", ".csv file format is not correct, upload canceled!");
	        this.doGet(request, response);
	        fileHandler.deleteOldFile(fileName);
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
