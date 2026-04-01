package ma.fst.sgcd.service;

import ma.fst.sgcd.model.RendezVous;
import ma.fst.sgcd.model.enums.StatutRDV;
import ma.fst.sgcd.repository.RendezVousRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RendezVousService {
    private final RendezVousRepository repo;

    public RendezVousService(RendezVousRepository repo) { this.repo = repo; }

    public List<RendezVous> findAll()                            { return repo.findAll(); }
    public Optional<RendezVous> findById(Long id)                { return repo.findById(id); }
    public List<RendezVous> findToday()                          { return repo.findByDate(LocalDate.now()); }
    public List<RendezVous> findByDate(LocalDate d)              { return repo.findByDate(d); }
    public List<RendezVous> findByPatient(Long idP)              { return repo.findByPatient(idP); }
    public List<RendezVous> findByWeek(LocalDate start, LocalDate end) { return repo.findByWeek(start, end); }
    public int countToday()                                      { return repo.countToday(); }

    public RendezVous planifier(RendezVous rv) {
        rv.setStatut(StatutRDV.PLANIFIE);
        return repo.save(rv);
    }

    public boolean annuler(Long id) {
        return repo.updateStatut(id, StatutRDV.ANNULE);
    }

    public boolean marquerArrivee(Long id) {
        return repo.updateStatut(id, StatutRDV.EN_SALLE_ATTENTE);
    }

    public boolean marquerEnCours(Long id) {
        return repo.updateStatut(id, StatutRDV.EN_COURS);
    }

    public boolean marquerTermine(Long id) {
        return repo.updateStatut(id, StatutRDV.TERMINE);
    }

    public boolean modifier(RendezVous rv) {
        return repo.update(rv);
    }
}
