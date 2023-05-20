package site_handling;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import helpers.FileHandler;
import helpers.MarketingHelper;
import user_handling.PasswordHasher;
import user_handling.SetupUser;
import user_handling.User;
import server_conf.ThymeleafConfig;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebContext context = new WebContext(request, response, request.getServletContext());
        response.setCharacterEncoding("UTF-8");
        ThymeleafConfig.getTemplateEngine().process("register.html", context, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        WebContext context = new WebContext(request, response, request.getServletContext());
        String name = request.getParameter("username");
        String password = request.getParameter("password");
        name = name.toLowerCase();
        PasswordHasher hasher = new PasswordHasher();
        SetupUser setup = new SetupUser(name, password ,hasher.hashPassword(password));
        
        User user = new User(name);
        FileHandler fileHandler = new FileHandler(user);
        if (setup.addUser(fileHandler)) {
        	
            MarketingHelper marketing = new MarketingHelper(user);
            marketing.newMarketingFile();
            response.sendRedirect("index");
            
        } else {
            context.setVariable("error", "1");
            context.setVariable("errorMsg", setup.getErrorMsg());
            ThymeleafConfig.getTemplateEngine().process("register.html", context, response.getWriter());
        }
    }
}
