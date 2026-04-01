package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.*;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * GET  /consultation?action=ouvrir&idRdv=X  → formulaire nouvelle consultation
 * GET  /consultation?action=detail&id=X     → détail consultation
 * POST /consultation?action=save            → enregistrer
 * POST /consultation?action=prescrire       → ajouter prescription
 */
@WebServlet("/consultation")
public class ConsultationServlet extends HttpServlet {

    private ConsultationService  service;
    private RendezVousService    rdvService;
    private PatientService       patientService;

    @Override
    public void init() {
        ConsultationRepository  cr  = new ConsultationRepository();
        FactureRepository       fr  = new FactureRepository();
        PrescriptionRepository  prr = new PrescriptionRepository();
        ActeRepository          ar  = new ActeRepository();
        DossierMedicalRepository dr = new DossierMedicalRepository();
        PatientRepository       pr  = new PatientRepository();

        service       = new ConsultationService(cr, fr, prr, ar, dr);
        rdvService    = new RendezVousService(new RendezVousRepository());
        patientService = new PatientService(pr, dr);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("detail".equals(action)) { showDetail(req, resp); return; }
        showForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if ("prescrire".equals(action)) { savePrescription(req, resp); return; }
        saveConsultation(req, resp);
    }

    // ─── Afficher formulaire ─────────────────────────────────────────────
    private void showForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idRdvParam = req.getParameter("idRdv");
        if (idRdvParam != null) {
            Long idRdv = Long.parseLong(idRdvParam);
            rdvService.findById(idRdv).ifPresent(rv -> req.setAttribute("rdv", rv));
        }
        req.setAttribute("actes", service.findAllActes());
        req.getRequestDispatcher("/views/consultation/form.jsp").forward(req, resp);
    }

    // ─── Afficher détail ─────────────────────────────────────────────────
    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Optional<Consultation> opt = service.findById(id);
        if (opt.isEmpty()) { resp.sendError(404); return; }
        Consultation c = opt.get();
        req.setAttribute("consultation", c);
        service.findPrescription(id).ifPresent(p -> req.setAttribute("prescription", p));

        // Charger facture liée
        new FactureRepository().findByConsultation(id)
            .ifPresent(f -> req.setAttribute("facture", f));

        req.getRequestDispatcher("/views/consultation/detail.jsp").forward(req, resp);
    }

    // ─── Enregistrer consultation ────────────────────────────────────────
    private void saveConsultation(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute("utilisateur");

        String idRdvParam = req.getParameter("idRdv");
        String idPatientParam = req.getParameter("idPatient");

        // Résoudre idDossier depuis le patient
        Long idDossier = null;
        if (idPatientParam != null && !idPatientParam.isBlank()) {
            Long idPatient = Long.parseLong(idPatientParam);
            Optional<Patient> pOpt = patientService.findWithDetails(idPatient);
            if (pOpt.isPresent() && pOpt.get().getDossierMedical() != null) {
                idDossier = pOpt.get().getDossierMedical().getIdDossier();
            }
        }
        // Fallback: idDossier passé directement
        if (idDossier == null) {
            String idDossierParam = req.getParameter("idDossier");
            if (idDossierParam != null && !idDossierParam.isBlank()) {
                idDossier = Long.parseLong(idDossierParam);
            }
        }

        if (idDossier == null) {
            req.setAttribute("error", "Impossible de trouver le dossier médical du patient.");
            showForm(req, resp);
            return;
        }

        Consultation cons = new Consultation();
        cons.setDate(LocalDate.now());
        cons.setDiagnostic(req.getParameter("diagnostic"));
        cons.setObservations(req.getParameter("observations"));
        cons.setIdDossier(idDossier);
        cons.setIdDentiste(u.getIdUtilisateur());

        if (idRdvParam != null && !idRdvParam.isBlank())
            cons.setIdRDV(Long.parseLong(idRdvParam));

        // Actes sélectionnés
        String[] codes = req.getParameterValues("actes");
        if (codes != null) {
            ActeRepository ar = new ActeRepository();
            for (String code : codes) {
                ar.findById(code).ifPresent(a -> cons.getActes().add(a));
            }
        }

        service.ouvrir(cons);

        // Marquer le RDV comme terminé
        if (cons.getIdRDV() != null) rdvService.marquerTermine(cons.getIdRDV());

        req.getSession().setAttribute("flash_success", "Consultation enregistrée avec succès.");
        resp.sendRedirect(req.getContextPath() + "/consultation?action=detail&id=" + cons.getIdConsultation());
    }

    // ─── Enregistrer prescription ────────────────────────────────────────
    private void savePrescription(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long idCons = Long.parseLong(req.getParameter("idConsultation"));

        Prescription pr = new Prescription();
        pr.setDate(LocalDate.now());
        pr.setInstructions(req.getParameter("instructions"));
        pr.setIdConsultation(idCons);

        String[] noms   = req.getParameterValues("med_nom");
        String[] doses  = req.getParameterValues("med_dosage");
        String[] durees = req.getParameterValues("med_duree");

        if (noms != null) {
            for (int i = 0; i < noms.length; i++) {
                if (noms[i] == null || noms[i].isBlank()) continue;
                Medicament m = new Medicament();
                m.setNom(noms[i].trim());
                m.setDosage(doses != null && doses.length > i ? doses[i] : "");
                try {
                    m.setDureeTraitement(durees != null && durees.length > i
                            ? Integer.parseInt(durees[i]) : 7);
                } catch (NumberFormatException e) { m.setDureeTraitement(7); }
                pr.addMedicament(m);
            }
        }

        service.prescrire(pr);
        req.getSession().setAttribute("flash_success", "Prescription enregistrée.");
        resp.sendRedirect(req.getContextPath() + "/consultation?action=detail&id=" + idCons);
    }
}
