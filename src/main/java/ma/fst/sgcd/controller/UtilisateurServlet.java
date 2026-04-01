package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.model.enums.Statut;
import ma.fst.sgcd.repository.UtilisateurRepository;
import ma.fst.sgcd.util.PasswordUtil;

import java.io.IOException;
import java.util.Optional;

/**
 * Réservé à l'ADMINISTRATEUR.
 * GET  /admin/utilisateurs              → liste
 * GET  /admin/utilisateurs?action=add   → formulaire ajout
 * GET  /admin/utilisateurs?action=edit&id=X → formulaire édition
 * POST /admin/utilisateurs?action=save   → créer
 * POST /admin/utilisateurs?action=update → mettre à jour
 * POST /admin/utilisateurs?action=toggle → activer/désactiver
 */
@WebServlet("/admin/utilisateurs")
public class UtilisateurServlet extends HttpServlet {

    private UtilisateurRepository repo;

    @Override
    public void init() { repo = new UtilisateurRepository(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        requireAdmin(req, resp);
        String action = req.getParameter("action");
        if (action == null) action = "list";
        switch (action) {
            case "add"  -> showAdd(req, resp);
            case "edit" -> showEdit(req, resp);
            default     -> showList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        requireAdmin(req, resp);
        String action = req.getParameter("action");
        switch (action == null ? "" : action) {
            case "save"   -> saveUser(req, resp);
            case "update" -> updateUser(req, resp);
            case "toggle" -> toggleStatut(req, resp);
            default       -> resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs");
        }
    }

    private void requireAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (u == null || u.getRole() != Role.ADMINISTRATEUR) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("utilisateurs", repo.findAll());
        req.getRequestDispatcher("/views/admin/utilisateurs/list.jsp").forward(req, resp);
    }

    private void showAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher("/views/admin/utilisateurs/form.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Optional<Utilisateur> opt = repo.findById(id);
        if (opt.isEmpty()) { resp.sendError(404); return; }
        req.setAttribute("utilisateur", opt.get());
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher("/views/admin/utilisateurs/form.jsp").forward(req, resp);
    }

    private void saveUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (repo.existsByLogin(req.getParameter("login"))) {
            req.getSession().setAttribute("flash_error", "Ce login existe déjà.");
            resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs?action=add");
            return;
        }
        Utilisateur u = buildFromRequest(req, new Utilisateur());
        u.setMotDePasse(PasswordUtil.hash(req.getParameter("password")));
        u.setStatut(Statut.ACTIF);
        repo.save(u);
        req.getSession().setAttribute("flash_success", "Utilisateur créé avec succès.");
        resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs");
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("idUtilisateur"));
        Optional<Utilisateur> opt = repo.findById(id);
        if (opt.isEmpty()) { resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs"); return; }
        Utilisateur u = buildFromRequest(req, opt.get());
        String newPwd = req.getParameter("password");
        if (newPwd != null && !newPwd.isBlank()) repo.updatePassword(id, PasswordUtil.hash(newPwd));
        repo.update(u);
        req.getSession().setAttribute("flash_success", "Utilisateur mis à jour.");
        resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs");
    }

    private void toggleStatut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        repo.findById(id).ifPresent(u -> {
            u.setStatut(u.getStatut() == Statut.ACTIF ? Statut.INACTIF : Statut.ACTIF);
            repo.update(u);
        });
        resp.sendRedirect(req.getContextPath() + "/admin/utilisateurs");
    }

    private Utilisateur buildFromRequest(HttpServletRequest req, Utilisateur u) {
        u.setNom(req.getParameter("nom").trim().toUpperCase());
        u.setPrenom(req.getParameter("prenom").trim());
        u.setEmail(req.getParameter("email").trim());
        u.setLogin(req.getParameter("login").trim());
        u.setRole(Role.valueOf(req.getParameter("role")));
        return u;
    }
}
