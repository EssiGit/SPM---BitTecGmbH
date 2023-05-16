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
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
	    User user = (User) session.getAttribute("User");
	    if (user == null) {
	        response.sendRedirect("index"); // Redirect to the "index" servlet
	        return;
	    }


	    WebContext context = new WebContext(request, response, request.getServletContext());
	    FileHandler filehandler = new FileHandler(user);

	    String buttonValue = request.getParameter("selectedButton");
	    if (buttonValue == null) {
	        buttonValue = (String) session.getAttribute("selButton");
	    }else {
	    	session.setAttribute("filename", buttonValue);
	    }
	    String typeOfAnalysis = request.getParameter("clusterInfo");
	    if (typeOfAnalysis == null) {
	        typeOfAnalysis = "Umsatzstärkstertag/Uhrzeit";
	    }

	    int clusterAnzahl = 8;
	    if (request.getParameter("sliderValue") != null) {
	        clusterAnzahl = Integer.parseInt(request.getParameter("sliderValue"));
	    }

	    try {
	        WekaAnalyser weka = new WekaAnalyser((String) session.getAttribute("filename"), user);
	        ArrayList<Weka_resultFile> wekaFiles = weka.getCorrectAnalysis(filehandler, typeOfAnalysis, clusterAnzahl);

	        if (request.getParameter("ajaxUpdate") != null && request.getParameter("ajaxUpdate").equals("1")) {
	            response.setContentType("application/json");
	            response.setContentLength(wekaFiles.get(0).ajax().length());
	            response.getWriter().write(wekaFiles.get(0).ajax());
	            return;
	        }

	        context.setVariable("buttons", filehandler.getFileNames());
	        context.setVariable("isCluster", !(typeOfAnalysis.equals("Umsatzstärkstertag/Uhrzeit") || 
	                                           typeOfAnalysis.equals("Kundenhäufigkeit") ||
	                                           typeOfAnalysis.equals("uhrzeitProTag")));
	        context.setVariable("margin", 120);
	        context.setVariable("typeOfAnalysis", typeOfAnalysis);
	        context.setVariable("results", wekaFiles);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
		watch.stop();
		System.out.println("Time till served: " + watch.getTime() + "ms");

	}


}
