package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.repository.UtilisateurRepository;
import ma.fst.sgcd.service.AuthService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private AuthService service;

    @Override
    public void init() {
        service = new AuthService(new UtilisateurRepository());
    }

    /** GET → affiche la page de connexion */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Si déjà connecté, rediriger vers dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("utilisateur") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    /** POST → traite les credentials */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String login    = req.getParameter("login");
        String password = req.getParameter("password");

        Optional<Utilisateur> opt = service.authenticate(login, password);

        if (opt.isEmpty()) {
            req.setAttribute("error", "Identifiants incorrects ou compte inactif.");
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Utilisateur u = opt.get();
        HttpSession session = req.getSession(true);
        session.setAttribute("utilisateur", u);
        session.setAttribute("role", u.getRole().name());
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
