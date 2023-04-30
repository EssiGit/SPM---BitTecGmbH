package site_handling;


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
		String fileName = request.getParameter("fileName");
        WebContext context = new WebContext(request, response,
                request.getServletContext());
        FileHandler filehandler = new FileHandler();
        //request.
        String[] buttonVal = filehandler.getFileNames();
        for(int i=1;i<=5;i++) {
        	context.setVariable("button"+i,buttonVal[i-1]);
        }
        
		try {
			WekaAnalyser weka = new WekaAnalyser(fileName);
			Weka_resultFile resFile = new Weka_resultFile(weka.clusterAnalyse(),1);
			//context.setVariable("results", results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
    }
	
}
