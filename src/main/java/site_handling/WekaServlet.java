package site_handling;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;   
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

@WebServlet("/WekaServlet")
public class WekaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebContext context = new WebContext(request, response,
				request.getServletContext());
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String buttonValue = request.getParameter("selectedButton");
		System.out.println("context: " + buttonValue);
		FileHandler filehandler = new FileHandler((User)session.getAttribute("User"));
		String[] buttonVal = filehandler.getFileNames();
		context.setVariable("buttons",buttonVal);
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		FileHandler filehandler = new FileHandler((User)session.getAttribute("User"));
		String[] buttonVal = filehandler.getFileNames();
		context.setVariable("buttons",buttonVal);

		try {
			WekaAnalyser weka = new WekaAnalyser(buttonValue,(User)session.getAttribute("User"));
			ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

			File resultSet = weka.clusterAnalyse(filehandler);
			int lines = getLines(resultSet);
			for(int i=1;i<lines;i++) {
				Weka_resultFile resFile = new Weka_resultFile(resultSet,i);
				wekaFiles.add(resFile);
			}
			for(Weka_resultFile test : wekaFiles) {
				System.out.println(test.getTableName());
			}
			context.setVariable("results", wekaFiles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
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
