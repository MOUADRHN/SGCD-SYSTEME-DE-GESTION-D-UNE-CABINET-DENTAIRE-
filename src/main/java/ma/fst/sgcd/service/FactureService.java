package ma.fst.sgcd.service;

import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Paiement;
import ma.fst.sgcd.model.enums.StatutFacture;
import ma.fst.sgcd.repository.FactureRepository;

import java.util.List;
import java.util.Optional;

public class FactureService {
    private final FactureRepository repo;

    public FactureService(FactureRepository repo) { this.repo = repo; }

    public List<Facture>     findAll()                     { return repo.findAll(); }
    public Optional<Facture> findById(Long id)             { return repo.findById(id); }
    public Optional<Facture> findByConsultation(Long idC)  { return repo.findByConsultation(idC); }
    public double            chiffreAffairesMois()         { return repo.chiffreAffairesMois(); }

    public Paiement enregistrerPaiement(Paiement p) {
        Paiement saved = repo.savePaiement(p);
        repo.updateStatut(p.getIdFacture(), StatutFacture.PAYEE);
        return saved;
    }
}
