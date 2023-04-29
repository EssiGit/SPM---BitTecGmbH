package site_handling;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import server_conf.ThymeleafConfig;
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
        ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("do post executed");
		Iterator<String> paras = request.getParameterNames().asIterator();
		
		while(paras.hasNext())
		{
			System.out.println(paras.next());
		}
		
		Part filePart = request.getPart("coverfile");
		
		System.out.println(filePart.getSize());
		String fileName = filePart.getSubmittedFileName();
		
		for(Part part : request.getParts()) {
			part.write(System.getenv("PATH") + File.separator + "KaufDort_Userfiles" + File.separator +fileName);
		}
    }

}