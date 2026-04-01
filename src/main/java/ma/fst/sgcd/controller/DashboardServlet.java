package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.*;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private PatientService      patientService;
    private RendezVousService   rdvService;
    private ConsultationService consultationService;
    private FactureService      factureService;

    @Override
    public void init() {
        PatientRepository        pr = new PatientRepository();
        DossierMedicalRepository dr = new DossierMedicalRepository();
        RendezVousRepository     rr = new RendezVousRepository();
        ConsultationRepository   cr = new ConsultationRepository();
        FactureRepository        fr = new FactureRepository();
        PrescriptionRepository   prr = new PrescriptionRepository();
        ActeRepository           ar = new ActeRepository();

        patientService      = new PatientService(pr, dr);
        rdvService          = new RendezVousService(rr);
        consultationService = new ConsultationService(cr, fr, prr, ar, dr);
        factureService      = new FactureService(fr);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Statistiques rapides
        req.setAttribute("rdvAujourd'hui",   rdvService.findToday());
        req.setAttribute("rdvToday",         rdvService.findToday());
        req.setAttribute("countPatients",    patientService.countAll());
        req.setAttribute("countRdvToday",    rdvService.countToday());
        req.setAttribute("countConsultMois", consultationService.countThisMonth());
        req.setAttribute("caMonth",          factureService.chiffreAffairesMois());
        req.getRequestDispatcher("/views/dashboard/index.jsp").forward(req, resp);
    }
}
