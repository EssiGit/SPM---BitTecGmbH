package site_handling;

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
        String[] buttonVal = filehandler.getFileNames();
        context.setVariable("buttons", buttonVal);
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

        WebContext context = new WebContext(request, response, request.getServletContext());
        FileHandler filehandler = new FileHandler(user);

        setButtonValues(context, filehandler);

        String buttonValue = request.getParameter("selectedButton");
        if (buttonValue != null) {
            System.out.println("buttonValue != null");
            System.out.println(buttonValue);
            session.setAttribute("filename", buttonValue);
        }

        String typeOfAnalysis = request.getParameter("clusterInfo");
        if (typeOfAnalysis == null) {
            typeOfAnalysis = "Umsatzstärkstertag/Uhrzeit";
        }

        int clusterAnzahl = getClusterAnzahl(request);

        try {
            WekaAnalyser weka = new WekaAnalyser((String) session.getAttribute("filename"), user);
            ArrayList<Weka_resultFile> wekaFiles = weka.getCorrectAnalysis(filehandler, typeOfAnalysis, clusterAnzahl);
            if (isAjaxUpdate(request)) {
                response.setContentType("application/json");
                response.setContentLength(wekaFiles.get(0).ajax().length());
                response.getWriter().write(wekaFiles.get(0).ajax());
                return;
            }

            setAnalysisVariables(context, filehandler, typeOfAnalysis, wekaFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThymeleafConfig.getTemplateEngine().process("main.html", context, response.getWriter());

        watch.stop();
        System.out.println("Full time till served: " + watch.getTime() + " ms");
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

    private void setButtonValues(WebContext context, FileHandler filehandler) throws IOException {
        String[] buttonVal = filehandler.getFileNames();
        context.setVariable("buttons", buttonVal);
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

