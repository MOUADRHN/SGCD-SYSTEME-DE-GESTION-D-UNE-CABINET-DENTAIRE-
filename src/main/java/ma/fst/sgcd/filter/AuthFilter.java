package ma.fst.sgcd.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.Utilisateur;

import java.io.IOException;

/**
 * Filtre d'authentification global.
 * Protège toutes les URLs sauf /auth, les ressources statiques et les pages d'erreur.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PATHS = {
        "/auth", "/css/", "/js/", "/images/", "/views/common/error"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Laisser passer les ressources publiques
        for (String pub : PUBLIC_PATHS) {
            if (path.startsWith(pub)) { chain.doFilter(request, response); return; }
        }

        // Vérifier la session
        HttpSession session = req.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth");
        } else {
            chain.doFilter(request, response);
        }
    }
}
