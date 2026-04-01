package ma.fst.sgcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Paiement;
import ma.fst.sgcd.model.enums.ModePaiement;
import ma.fst.sgcd.repository.FactureRepository;
import ma.fst.sgcd.service.FactureService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * GET  /facture               → liste
 * GET  /facture?action=detail&id=X → détail
 * POST /facture?action=payer  → enregistrer paiement
 */
@WebServlet("/facture")
public class FactureServlet extends HttpServlet {

    private FactureService service;

    @Override
    public void init() { service = new FactureService(new FactureRepository()); }

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
        if ("payer".equals(action)) { enregistrerPaiement(req, resp); return; }
        resp.sendRedirect(req.getContextPath() + "/facture");
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("factures", service.findAll());
        req.getRequestDispatcher("/views/facture/list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Optional<Facture> opt = service.findById(id);
        if (opt.isEmpty()) { resp.sendError(404); return; }
        req.setAttribute("facture", opt.get());
        req.setAttribute("modesPaiement", ModePaiement.values());
        req.getRequestDispatcher("/views/facture/detail.jsp").forward(req, resp);
    }

    private void enregistrerPaiement(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Paiement p = new Paiement();
        p.setIdFacture(Long.parseLong(req.getParameter("idFacture")));
        p.setMontant(Double.parseDouble(req.getParameter("montant")));
        p.setModePaiement(ModePaiement.valueOf(req.getParameter("modePaiement")));
        p.setDatePaiement(LocalDate.now());
        service.enregistrerPaiement(p);
        req.getSession().setAttribute("flash_success", "Paiement enregistré. Facture soldée.");
        resp.sendRedirect(req.getContextPath() + "/facture?action=detail&id=" + p.getIdFacture());
    }
}
