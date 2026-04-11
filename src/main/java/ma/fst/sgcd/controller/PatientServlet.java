package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.*;
import ma.fst.sgcd.model.enums.Sexe;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

/**
 * Gestion CRUD des patients.
 * GET  /patients                    → liste (+ ?q= recherche)
 * GET  /patients?action=add         → formulaire ajout
 * POST /patients?action=save        → enregistrer
 * GET  /patients?action=edit&id=X   → formulaire édition
 * POST /patients?action=update      → mettre à jour
 * GET  /patients?action=detail&id=X → dossier complet
 * POST /patients?action=delete&id=X → supprimer
 */
@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private PatientService      service;
    private RendezVousService   rdvService;
    private ConsultationService consultService;
    private PatientRepository   patientRepo;

    @Override
    public void init() {
        PatientRepository        pr  = new PatientRepository();
        DossierMedicalRepository dr  = new DossierMedicalRepository();
        RendezVousRepository     rr  = new RendezVousRepository();
        ConsultationRepository   cr  = new ConsultationRepository();
        FactureRepository        fr  = new FactureRepository();
        PrescriptionRepository   prr = new PrescriptionRepository();
        ActeRepository           ar  = new ActeRepository();
        service        = new PatientService(pr, dr);
        rdvService     = new RendezVousService(rr);
        consultService = new ConsultationService(cr, fr, prr, ar, dr);
        patientRepo    = pr;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        switch (action) {
            case "add"    -> req.getRequestDispatcher("/views/patient/add.jsp").forward(req, resp);
            case "edit"   -> showEdit(req, resp);
            case "detail" -> showDetail(req, resp);
            default       -> showList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "save";
        switch (action) {
            case "save"   -> savePatient(req, resp);
            case "update" -> updatePatient(req, resp);
            case "delete" -> deletePatient(req, resp);
            default       -> resp.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    // ─── Liste ──────────────────────────────────────────────────────────
    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");
        var patients = (q != null && !q.isBlank()) ? service.search(q) : service.findAll();
        req.setAttribute("patients",     patients);
        req.setAttribute("searchQuery",  q);
        req.getRequestDispatcher("/views/patient/list.jsp").forward(req, resp);
    }

    // ─── Formulaire édition ─────────────────────────────────────────────
    private void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Optional<Patient> opt = service.findWithDetails(id);
        if (opt.isEmpty()) { resp.sendError(404); return; }
        req.setAttribute("patient", opt.get());
        req.getRequestDispatcher("/views/patient/edit.jsp").forward(req, resp);
    }

    // ─── Détail ─────────────────────────────────────────────────────────
    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Optional<Patient> opt = service.findWithDetails(id);
        if (opt.isEmpty()) { resp.sendError(404); return; }
        Patient p = opt.get();
        req.setAttribute("patient", p);
        if (p.getDossierMedical() != null) {
            req.setAttribute("consultations",
                    consultService.findByDossier(p.getDossierMedical().getIdDossier()));
        }
        req.setAttribute("rdvList", rdvService.findByPatient(id));
        req.getRequestDispatcher("/views/patient/detail.jsp").forward(req, resp);
    }

    // ─── Enregistrer ────────────────────────────────────────────────────
    private void savePatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        // ── Validation âge (CDC : 0 < âge < 120 ans) ────────────────────
        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(req.getParameter("dateNaissance"));
        } catch (Exception e) {
            req.setAttribute("error", "Date de naissance invalide.");
            req.getRequestDispatcher("/views/patient/add.jsp").forward(req, resp);
            return;
        }
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();
        if (age < 0 || age >= 120) {
            req.setAttribute("error", "Âge invalide : doit être compris entre 0 et 120 ans.");
            req.getRequestDispatcher("/views/patient/add.jsp").forward(req, resp);
            return;
        }

        // ── Vérification doublon (CDC : nom + date naissance) ────────────
        String nom    = req.getParameter("nom").trim().toUpperCase();
        String prenom = req.getParameter("prenom").trim();
        if (patientRepo.existsByNomDateNaissance(nom, prenom, dateNaissance)) {
            req.setAttribute("error",
                "Un patient avec le même nom et la même date de naissance existe déjà.");
            req.getRequestDispatcher("/views/patient/add.jsp").forward(req, resp);
            return;
        }

        Patient p = buildFromRequest(req, new Patient());
        ResponsableLegal rl = buildRL(req);
        service.enregistrer(p, rl);
        req.getSession().setAttribute("flash_success", "Patient enregistré avec succès.");
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    // ─── Mettre à jour ──────────────────────────────────────────────────
    private void updatePatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        Long idPatient = Long.parseLong(req.getParameter("idPatient"));

        // ── Validation âge ───────────────────────────────────────────────
        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(req.getParameter("dateNaissance"));
        } catch (Exception e) {
            req.setAttribute("error", "Date de naissance invalide.");
            showEdit(req, resp);
            return;
        }
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();
        if (age < 0 || age >= 120) {
            req.setAttribute("error", "Âge invalide : doit être compris entre 0 et 120 ans.");
            showEdit(req, resp);
            return;
        }

        // ── Vérification doublon (en excluant le patient actuel) ─────────
        String nom    = req.getParameter("nom").trim().toUpperCase();
        String prenom = req.getParameter("prenom").trim();
        if (patientRepo.existsByNomDateNaissanceExcept(nom, prenom, dateNaissance, idPatient)) {
            req.setAttribute("error",
                "Un autre patient avec le même nom et la même date de naissance existe déjà.");
            showEdit(req, resp);
            return;
        }

        Patient p = buildFromRequest(req, new Patient());
        p.setIdPatient(idPatient);
        ResponsableLegal rl = buildRL(req);
        service.modifier(p, rl);
        req.getSession().setAttribute("flash_success", "Patient mis à jour avec succès.");
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    // ─── Supprimer ──────────────────────────────────────────────────────
    private void deletePatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        patientRepo.delete(id);
        req.getSession().setAttribute("flash_success", "Patient supprimé.");
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    // ─── Helpers ────────────────────────────────────────────────────────
    private Patient buildFromRequest(HttpServletRequest req, Patient p) {
        p.setNom(req.getParameter("nom").trim().toUpperCase());
        p.setPrenom(req.getParameter("prenom").trim());
        p.setDateNaissance(LocalDate.parse(req.getParameter("dateNaissance")));
        p.setSexe(Sexe.valueOf(req.getParameter("sexe")));
        p.setAdresse(req.getParameter("adresse"));
        p.setTelephone(req.getParameter("telephone"));
        p.setNumeroCNSS(req.getParameter("numeroCNSS"));
        p.setAntecedents(req.getParameter("antecedents"));
        p.setAllergie(req.getParameter("allergie"));
        return p;
    }

    private ResponsableLegal buildRL(HttpServletRequest req) {
        String nomRL = req.getParameter("rl_nom");
        if (nomRL == null || nomRL.isBlank()) return null;
        ResponsableLegal rl = new ResponsableLegal();
        rl.setNom(nomRL.trim());
        rl.setTelephone(req.getParameter("rl_telephone"));
        rl.setLienParente(req.getParameter("rl_lienParente"));
        return rl;
    }
}
