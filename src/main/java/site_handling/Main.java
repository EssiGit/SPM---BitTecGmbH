package site_handling;


import java.io.File; 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import helpers.FileHandler;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.ArrayList;
import server_conf.ThymeleafConfig;
import java.util.Arrays;
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
        FileHandler filehandler = new FileHandler();
       
        List<String> buttonVal = new ArrayList<>();
        buttonVal = Arrays.asList(filehandler.getFileNames());
        //for(int i=1;i<=5;i++) {
        	context.setVariable("buttons",buttonVal);
        //}
        ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(request, response,
                request.getServletContext());
        
		System.out.println("do post executed");

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
		
		System.out.println(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator +fileName);
		
		File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles");
		
		if(!(DIR.exists())) {
			File tmp = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "Result_Files");
			tmp.mkdirs();
		}
		DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator +fileName);
		if(!(DIR.exists())) {
			System.out.println("its a new file");
			for(Part part : request.getParts()) {
				part.write(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator +fileName);
			}
		}
		this.doGet(request,response);
    }
	


}
