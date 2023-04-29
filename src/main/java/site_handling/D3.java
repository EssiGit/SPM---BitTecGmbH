package site_handling;


import java.io.IOException;  
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server_conf.ThymeleafConfig;
import org.thymeleaf.context.WebContext;


@WebServlet("/d3")
public class D3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext context = new WebContext(request, response,
                request.getServletContext());
        response.setCharacterEncoding("UTF-8");
        ThymeleafConfig.getTemplateEngine().process("d3Test.html", context, response.getWriter());
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
