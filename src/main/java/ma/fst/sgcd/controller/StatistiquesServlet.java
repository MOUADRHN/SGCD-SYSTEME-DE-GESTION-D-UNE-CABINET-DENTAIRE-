package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.*;

import java.io.IOException;

/** Tableau de bord statistiques — réservé à l'administrateur. */
@WebServlet("/statistiques")
public class StatistiquesServlet extends HttpServlet {

    private PatientService    patientService;
    private RendezVousService rdvService;
    private FactureService    factureService;
    private ConsultationService consultService;

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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("totalPatients",    patientService.countAll());
        req.setAttribute("rdvToday",         rdvService.countToday());
        req.setAttribute("consultsMois",     consultService.countThisMonth());
        req.setAttribute("caMois",           factureService.chiffreAffairesMois());
        req.setAttribute("rdvList",          rdvService.findToday());
        req.getRequestDispatcher("/views/statistiques/index.jsp").forward(req, resp);
    }
}
