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
import weka.WekaAnalyser;
import weka.Weka_resultFile;
import user_handling.User;
import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;

@WebServlet("/WekaServlet")
public class WekaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("User");

		if (redirect(user, response)) {
			return;
		}

		WebContext context = new WebContext(request, response, request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");

		FileHandler filehandler = new FileHandler(user);
		System.out.println("WekaServlet doGet");
		context.setVariable("buttons", filehandler.getFileNames());
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StopWatch watch = new StopWatch();
		watch.start();
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("User");
		if (redirect(user, response)) {
			return;
		}
		FileHandler filehandler = new FileHandler(user);

		WebContext context = new WebContext(request, response, request.getServletContext());	
		context.setVariable("buttons", filehandler.getFileNames());
		String buttonValue = request.getParameter("selectedButton");
		if (buttonValue != null) {
			session.setAttribute("filename", buttonValue);
		}

		String typeOfAnalysis = request.getParameter("clusterInfo");
		if (typeOfAnalysis == null) {
			typeOfAnalysis = "Umsatzstärkstertag/Uhrzeit";
		}

		
		int clusterAnzahl = getClusterAnzahl(request);
		WekaAnalyser weka = new WekaAnalyser((String) session.getAttribute("filename"), user);
		
		
		if(!checkForFile(weka,request,response,context,filehandler)) {
			return;
		}
		ArrayList<Weka_resultFile> wekaFiles = weka.getCorrectAnalysis(typeOfAnalysis, clusterAnzahl);
		if (isAjaxUpdate(request)) {
			sendAjaxResponse(response, wekaFiles);
			return;
		}
		setAnalysisVariables(context, filehandler, typeOfAnalysis, wekaFiles);
		
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
		watch.stop();
		System.out.println("Full time till served: " + watch.getTime() + " ms");
	}

	/**
	 * checks if the data Object for Weka exists. If not, it should handle it. 
	 * Example: For some reason a local .csv got deleted by accident.
	 * @param weka
	 * @param request
	 * @param response
	 * @param context
	 * @param filehandler
	 * @return
	 * @throws IOException
	 */
	private boolean checkForFile(WekaAnalyser weka,HttpServletRequest request, HttpServletResponse response,WebContext context,FileHandler filehandler) throws IOException{
		if(!weka.dataNotNull()) {
			System.out.println("its null");
			request.setAttribute("error", "Datei enthält Fehler oder existiert nicht!");
			try {
				filehandler.keepFilesEqualToDIR();
				context.setVariable("buttons", filehandler.getFileNames());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
			return false;
		}
		return true;
	}
	/**
	 * Redirects to Index Servlet
	 *
	 * @param user     the User object
	 * @param response the HttpServletResponse object
	 * @return true if redirection occurs, false otherwise
	 * @throws IOException
	 */
	public boolean redirect(User user, HttpServletResponse response) throws IOException {
		if (user == null) {
			response.sendRedirect("index");
			return true;
		} else {
			return false;
		}
	}

	/*
	 * send out ajax response
	 */
	private void sendAjaxResponse(HttpServletResponse response, ArrayList<Weka_resultFile> wekaFiles) throws IOException {
		response.setContentType("application/json");
		String ajaxResponse = wekaFiles.get(0).ajax();
		response.setContentLength(ajaxResponse.length());
		response.getWriter().write(ajaxResponse);
	}

	private int getClusterAnzahl(HttpServletRequest request) {
		String sliderValue = request.getParameter("sliderValue");
		int clusterAnzahl = 8;
		if (sliderValue != null) {
			clusterAnzahl = Integer.parseInt(sliderValue);
		}
		return clusterAnzahl;
	}

	private boolean isAjaxUpdate(HttpServletRequest request) {
		return request.getParameter("ajaxUpdate") != null && request.getParameter("ajaxUpdate").equals("1");
	}

	private void setAnalysisVariables(WebContext context, FileHandler filehandler, String typeOfAnalysis, ArrayList<Weka_resultFile> wekaFiles) throws IOException {

		context.setVariable("buttons", filehandler.getFileNames());

		context.setVariable("isCluster", !(typeOfAnalysis.equals("Umsatzstärkstertag/Uhrzeit") ||
				typeOfAnalysis.equals("Kundenhäufigkeit") ||
				typeOfAnalysis.equals("uhrzeitProTag")));
		context.setVariable("margin", 120);
		context.setVariable("typeOfAnalysis", typeOfAnalysis);
		context.setVariable("results", wekaFiles);
	}
}

