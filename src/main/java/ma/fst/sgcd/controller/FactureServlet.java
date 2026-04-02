package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Paiement;
import ma.fst.sgcd.model.Patient;
import ma.fst.sgcd.model.enums.ModePaiement;
import ma.fst.sgcd.repository.FactureRepository;
import ma.fst.sgcd.repository.PatientRepository;
import ma.fst.sgcd.service.EmailService;
import ma.fst.sgcd.service.FactureService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GET  /facture                          → liste (+ ?q= recherche)
 * GET  /facture?action=detail&id=X       → détail facture
 * POST /facture?action=payer             → enregistrer paiement
 * POST /facture?action=email&id=X        → envoyer email reçu
 */
@WebServlet("/facture")
public class FactureServlet extends HttpServlet {

    private FactureService    service;
    private EmailService      emailService;
    private PatientRepository patientRepo;

    @Override
    public void init() {
        service      = new FactureService(new FactureRepository());
        emailService = new EmailService();
        patientRepo  = new PatientRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("detail".equals(action)) { showDetail(req, resp); return; }
        showList(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        switch (action == null ? "" : action) {
            case "payer" -> enregistrerPaiement(req, resp);
            case "email" -> envoyerEmail(req, resp);
            default      -> resp.sendRedirect(req.getContextPath() + "/facture");
        }
    }

    // ─── Liste avec recherche ─────────────────────────────────────────────
    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");
        List<Facture> factures = service.findAll();

        if (q != null && !q.isBlank()) {
            String qLow = q.toLowerCase();
            factures = factures.stream().filter(f ->
                (f.getNomPatient()    != null && f.getNomPatient().toLowerCase().contains(qLow)) ||
                (f.getPrenomPatient() != null && f.getPrenomPatient().toLowerCase().contains(qLow)) ||
                String.valueOf(f.getIdFacture()).contains(qLow) ||
                f.getStatut().getLibelle().toLowerCase().contains(qLow)
            ).collect(Collectors.toList());
        }

        req.setAttribute("factures",    factures);
        req.setAttribute("searchQuery", q);
        req.getRequestDispatcher("/views/facture/list.jsp").forward(req, resp);
    }

    // ─── Détail ───────────────────────────────────────────────────────────
    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        // Sécurité : id manquant ou vide → redirection liste
        if (idParam == null || idParam.isBlank()) {
            req.getSession().setAttribute("flash_error",
                    "Identifiant de facture manquant.");
            resp.sendRedirect(req.getContextPath() + "/facture");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Identifiant de facture invalide : " + idParam);
            return;
        }

        Optional<Facture> opt = service.findById(id);
        if (opt.isEmpty()) {
            resp.sendError(404, "Facture introuvable : id=" + id);
            return;
        }

        req.setAttribute("facture",       opt.get());
        req.setAttribute("modesPaiement", ModePaiement.values());
        req.getRequestDispatcher("/views/facture/detail.jsp").forward(req, resp);
    }

    // ─── Enregistrer paiement ─────────────────────────────────────────────
    private void enregistrerPaiement(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Paiement p = new Paiement();
            p.setIdFacture(Long.parseLong(req.getParameter("idFacture")));
            p.setMontant(Double.parseDouble(req.getParameter("montant")));
            p.setModePaiement(ModePaiement.valueOf(req.getParameter("modePaiement")));
            p.setDatePaiement(LocalDate.now());
            service.enregistrerPaiement(p);
            req.getSession().setAttribute("flash_success",
                    "Paiement enregistré avec succès. Facture soldée.");
            resp.sendRedirect(req.getContextPath()
                    + "/facture?action=detail&id=" + p.getIdFacture());
        } catch (Exception e) {
            req.getSession().setAttribute("flash_error",
                    "Erreur lors du paiement : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/facture");
        }
    }

    // ─── Envoyer email ────────────────────────────────────────────────────
    private void envoyerEmail(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/facture");
            return;
        }

        Long idFacture = Long.parseLong(idParam);
        Optional<Facture> optF = service.findById(idFacture);

        if (optF.isEmpty()) {
            req.getSession().setAttribute("flash_error", "Facture introuvable.");
            resp.sendRedirect(req.getContextPath() + "/facture");
            return;
        }

        Facture facture = optF.get();
        Patient patient = null;
        try {
            if (facture.getNomPatient() != null) {
                var results = patientRepo.search(facture.getNomPatient());
                if (!results.isEmpty()) patient = results.get(0);
            }
        } catch (Exception ignored) {}

        boolean ok = emailService.sendFacture(patient, facture);

        if (ok) {
            new FactureRepository().marquerEmailEnvoye(idFacture);
            req.getSession().setAttribute("flash_success",
                    "Reçu envoyé par email (simulation). Consultez les logs Tomcat.");
        } else {
            req.getSession().setAttribute("flash_error",
                    "Erreur lors de l'envoi de l'email.");
        }

        resp.sendRedirect(req.getContextPath()
                + "/facture?action=detail&id=" + idFacture);
    }
}
