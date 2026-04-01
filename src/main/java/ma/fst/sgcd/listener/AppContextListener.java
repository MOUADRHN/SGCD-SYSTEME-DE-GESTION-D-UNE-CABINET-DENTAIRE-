package ma.fst.sgcd.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ma.fst.sgcd.util.DBUtil;

/**
 * Initialise le DataSource MySQL au démarrage de l'application.
 * Les paramètres sont lus depuis web.xml (context-param).
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String url  = ctx.getInitParameter("db.url");
        String user = ctx.getInitParameter("db.user");
        String pass = ctx.getInitParameter("db.password");

        DBUtil.init(url, user, pass);
        ctx.log("[SGCD] DataSource MySQL initialisé avec succès → " + url);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[SGCD] Application arrêtée.");
    }
}
