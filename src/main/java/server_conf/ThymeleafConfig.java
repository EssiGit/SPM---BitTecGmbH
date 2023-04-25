package server_conf;
import java.io.File;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
@WebListener
public class ThymeleafConfig implements ServletContextListener {
	private static final Logger log = Logger.getLogger(ThymeleafConfig.class.getName());
	private static TemplateEngine engine;

	// wird bei Start des Servers aufgerufen
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Anwendung gestartet.");
		log.info(sce.getServletContext().getServerInfo());
		engine = templateEngine(sce.getServletContext());
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
	public static TemplateEngine getTemplateEngine() {
		return engine;
	}
	
	// erzeugt TemplateEngine
	private TemplateEngine templateEngine(ServletContext servletContext) {
		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolver(servletContext));
		return engine;
	}
	//legt Verzeichnis für Templates fest
	private ITemplateResolver templateResolver(ServletContext servletContext) {
		ServletContextTemplateResolver resolver = new
				ServletContextTemplateResolver(servletContext);
		resolver.setPrefix( File.separator+ "html"+  File.separator);
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}
}