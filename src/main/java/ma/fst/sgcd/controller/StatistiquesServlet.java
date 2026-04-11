package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.enums.StatutRDV;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.*;

import java.io.IOException;

/**
 * Tableau de bord statistiques — réservé à l'Administrateur (CDC).
 *
 * Statistiques exposées :
 *   - Nombre total de patients
 *   - RDV du jour
 *   - Consultations ce mois
 *   - Chiffre d'affaires global ce mois
 *   - Chiffre d'affaires par dentiste (CDC)
 *   - % RDV annulés / taux d'absences (CDC)
 */
@WebServlet("/statistiques")
public class StatistiquesServlet extends HttpServlet {

    private PatientService      patientService;
    private RendezVousService   rdvService;
    private FactureService      factureService;
    private ConsultationService consultService;
    private RendezVousRepository rdvRepo;
    private FactureRepository    factureRepo;

    @Override
    public void init() {
        PatientRepository        pr  = new PatientRepository();
        DossierMedicalRepository dr  = new DossierMedicalRepository();
        RendezVousRepository     rr  = new RendezVousRepository();
        ConsultationRepository   cr  = new ConsultationRepository();
        FactureRepository        fr  = new FactureRepository();
        PrescriptionRepository   prr = new PrescriptionRepository();
        ActeRepository           ar  = new ActeRepository();

        patientService  = new PatientService(pr, dr);
        rdvService      = new RendezVousService(rr);
        factureService  = new FactureService(fr);
        consultService  = new ConsultationService(cr, fr, prr, ar, dr);
        rdvRepo         = rr;
        factureRepo     = fr;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── KPI de base ───────────────────────────────────────────────────
        req.setAttribute("totalPatients",  patientService.countAll());
        req.setAttribute("rdvToday",       rdvService.countToday());
        req.setAttribute("consultsMois",   consultService.countThisMonth());
        req.setAttribute("caMois",         factureService.chiffreAffairesMois());

        // ── CA par dentiste (CDC) ─────────────────────────────────────────
        req.setAttribute("caParDentiste",  factureRepo.caParDentiste());

        // ── Taux RDV annulés (CDC : % RDV annulés) ───────────────────────
        int total     = rdvRepo.countTotal();
        int annules   = rdvRepo.countByStatut(StatutRDV.ANNULE);
        int nonHonore = rdvRepo.countByStatut(StatutRDV.NON_HONORE);
        double tauxAnnules    = total > 0 ? (annules  * 100.0 / total) : 0;
        double tauxNonHonore  = total > 0 ? (nonHonore* 100.0 / total) : 0;
        req.setAttribute("totalRdv",       total);
        req.setAttribute("nbAnnules",      annules);
        req.setAttribute("nbNonHonore",    nonHonore);
        req.setAttribute("tauxAnnules",    String.format("%.1f", tauxAnnules));
        req.setAttribute("tauxNonHonore",  String.format("%.1f", tauxNonHonore));

        // ── Agenda du jour ───────────────────────────────────────────────
        req.setAttribute("rdvList",        rdvService.findToday());

        req.getRequestDispatcher("/views/statistiques/index.jsp").forward(req, resp);
    }
}
