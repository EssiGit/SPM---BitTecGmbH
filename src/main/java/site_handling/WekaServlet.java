package site_handling;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import server_conf.ThymeleafConfig;
import org.thymeleaf.context.WebContext;
import weka.WekaAnalyser;
import weka.Weka_resultFile;
import helpers.FileHandler;
import helpers.User;
import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

@WebServlet("/WekaServlet")
public class WekaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		User user = (User)session.getAttribute("User");
	    if (user == null) {
	        response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
	        return;
	    }
		FileHandler filehandler = new FileHandler((User)session.getAttribute("User"));
		System.out.println("WekaServlet doGet");
		String[] buttonVal = filehandler.getFileNames();
		context.setVariable("buttons",buttonVal);
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StopWatch watch = new StopWatch();
		watch.start();
		HttpSession session = request.getSession();

		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		String buttonValue = request.getParameter("selectedButton");
		System.out.println("butt: " + buttonValue);
		System.out.println((String)session.getAttribute("selButton"));
		if(buttonValue == null) {
			buttonValue = (String)session.getAttribute("selButton");
		}

		System.out.println("context: " + buttonValue);
		User user = (User)session.getAttribute("User");
	    if (user == null) {
	        response.sendRedirect("index"); // Weiterleitung zum "index" Servlet
	        return;
	    }
		FileHandler filehandler = new FileHandler(user);
		String[] buttonVal = filehandler.getFileNames();
		context.setVariable("buttons",buttonVal);
		if(buttonValue != null) {
			session.setAttribute("filename", buttonValue);
		}
		try {
			WekaAnalyser weka = new WekaAnalyser((String)session.getAttribute("filename"),user);
			String typeOfAnalysis = request.getParameter("clusterInfo");
			if(typeOfAnalysis == null)
				typeOfAnalysis = "Umsatzstärkstertag/Uhrzeit";
			
			//(nikok)
			int clusterAnzahl = 8;
			if(request.getParameter("sliderValue") != null)
				clusterAnzahl = Integer.parseInt(request.getParameter("sliderValue"));
			
			ArrayList<Weka_resultFile> wekaFiles = weka.getCorrectAnalysis(filehandler, typeOfAnalysis, clusterAnzahl);
			
			//(nikok) ajax json response für table update
			if(request.getParameter("ajaxUpdate") != null && request.getParameter("ajaxUpdate").equals("1")) {
				
				response.setContentType("application/json");
				response.setContentLength(wekaFiles.get(0).ajax().length());
				response.getWriter().write(wekaFiles.get(0).ajax());
				System.out.println(wekaFiles.get(0).ajax());
				
				ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
				watch.stop();
				System.out.println("Time till served: " + watch.getTime() + "ms");
				return;
			}
			
			//setzen ob cluster oder nicht (nikok)
			if(!(typeOfAnalysis.equals("Umsatzstärkstertag/Uhrzeit") || 
					typeOfAnalysis.equals("Kundenhäufigkeit") ||
					typeOfAnalysis.equals("uhrzeitProTag"))) 
				context.setVariable("isCluster", true);
			
			//(nikok)
			context.setVariable("margin", 120);
			context.setVariable("typeOfAnalysis", typeOfAnalysis);
			
			context.setVariable("results", wekaFiles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		watch.stop();

		
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
		System.out.println("Time till served: " + watch.getTime() + "ms");

	}
	private int getLines(File file) throws FileNotFoundException, IOException { 
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int readingLine = 0;
			while((reader.readLine()) != null) {
				readingLine++;
			}
			return readingLine;
		}
	}

}
