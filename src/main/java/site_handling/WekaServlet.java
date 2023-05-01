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
import server_conf.ThymeleafConfig;
import org.thymeleaf.context.WebContext;
import weka.WekaAnalyser;
import weka.Weka_resultFile;
import helpers.FileHandler;
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
        FileHandler filehandler = new FileHandler();
       
        String[] buttonVal = filehandler.getFileNames();
        for(int i=1;i<=5;i++) {
        	context.setVariable("button"+i,buttonVal[i-1]);
        }
        ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext context = new WebContext(request, response,
                request.getServletContext());
		String fileName = "";
        for(int i=1;i<=5;i++) {
        	if(request.getParameter("button"+i) != null) {
        		fileName = request.getParameter("button"+i);
        		System.out.println("context: " + fileName);
        	}
        }
        /*fileName = request.getParameter("button2");
        Map<String,String[]> tmp = request.getParameterMap();
        for(Map.Entry<String,String[]> entry : tmp.entrySet()){
        	System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        Enumeration<String> paras;
        paras = request.getParameterNames();
        while (paras.hasMoreElements()) {
            System.out.println("test: " + paras.nextElement());
        }
        System.out.println("is empty? " + fileName);*/
		//fileName = "kd100.csv";

        FileHandler filehandler = new FileHandler();
        String[] buttonVal = filehandler.getFileNames();
        for(int i=1;i<=5;i++) {
        	context.setVariable("button"+i,buttonVal[i-1]);
        }
        
		try {
			WekaAnalyser weka = new WekaAnalyser(fileName);
			ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

			File resultSet = weka.clusterAnalyse();
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
