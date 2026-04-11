package ma.fst.sgcd.service;

import ma.fst.sgcd.model.RendezVous;
import ma.fst.sgcd.model.enums.StatutRDV;
import ma.fst.sgcd.repository.RendezVousRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    public boolean hasRdvEnCoursForDentiste(Long idDentiste) {
        return repo.hasRdvEnCoursForDentiste(idDentiste);
    }

    public boolean marquerTermine(Long id) {
        return repo.updateStatut(id, StatutRDV.TERMINE);
    }

    public boolean marquerNonHonore(Long id) {
        return repo.updateStatut(id, StatutRDV.NON_HONORE);
    }

    public List<RendezVous> findTodayByDentiste(Long idDentiste) { return repo.findTodayByDentiste(idDentiste); }
    public int countTodayByDentiste(Long idDentiste) { return repo.countTodayByDentiste(idDentiste); }
    public List<RendezVous> findByDateAndDentiste(LocalDate d, Long idDentiste) { return repo.findByDateAndDentiste(d, idDentiste); }

    public boolean modifier(RendezVous rv) {
        return repo.update(rv);
    }

    public void annulerRdvDepasses() {
        repo.annulerRdvDepasses();
    }

    // NOUVELLE MÉTHODE GÉRANT LA DURÉE ET LES CHEVAUCHEMENTS
    public List<String> getCreneauxDisponibles(Long idDentiste, LocalDate date, int dureeSelectionnee) {
        // Nécessite la méthode findByDentisteAndDate dans RendezVousRepository
        List<RendezVous> rdvsDuJour = repo.findByDentisteAndDate(idDentiste, date);
        List<String> creneauxLibres = new ArrayList<>();

        LocalTime heureActuelle = LocalTime.of(9, 0);
        LocalTime finJournee = LocalTime.of(19, 0);
        LocalTime now = LocalTime.now();
        boolean isToday = date.equals(LocalDate.now());

        while (!heureActuelle.plusMinutes(dureeSelectionnee).isAfter(finJournee)) {

            // Exclure la pause déjeuner (12h - 14h)
            if (heureActuelle.isBefore(LocalTime.of(14, 0)) && heureActuelle.plusMinutes(dureeSelectionnee).isAfter(LocalTime.of(12, 0))) {
                heureActuelle = heureActuelle.plusMinutes(15);
                continue;
            }

            LocalTime finPrevue = heureActuelle.plusMinutes(dureeSelectionnee);
            boolean estLibre = true;

            // Vérification des heures passées pour aujourd'hui
            if (isToday && heureActuelle.isBefore(now)) {
                estLibre = false;
            } else {
                // Vérification des chevauchements avec les autres RDV
                for (RendezVous rdv : rdvsDuJour) {
                    LocalTime rdvStart = rdv.getDateHeure().toLocalTime();
                    LocalTime rdvEnd = rdvStart.plusMinutes(rdv.getDuree());

                    if (heureActuelle.isBefore(rdvEnd) && finPrevue.isAfter(rdvStart)) {
                        estLibre = false;
                        break;
                    }
                }
            }

            if (estLibre) {
                creneauxLibres.add(heureActuelle.toString());
            }

            heureActuelle = heureActuelle.plusMinutes(15);
        }

        return creneauxLibres;
    }
}