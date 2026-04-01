package ma.fst.sgcd.service;

import ma.fst.sgcd.model.DossierMedical;
import ma.fst.sgcd.model.Patient;
import ma.fst.sgcd.model.ResponsableLegal;
import ma.fst.sgcd.repository.DossierMedicalRepository;
import ma.fst.sgcd.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientService {
    private final PatientRepository        patientRepo;
    private final DossierMedicalRepository dossierRepo;

    public PatientService(PatientRepository pr, DossierMedicalRepository dr) {
        this.patientRepo = pr;
        this.dossierRepo = dr;
    }

    public List<Patient> findAll()                           { return patientRepo.findAll(); }
    public Optional<Patient> findById(Long id)               { return patientRepo.findById(id); }
    public List<Patient> search(String q)                    { return patientRepo.search(q); }
    public int countAll()                                    { return patientRepo.countAll(); }

    /** Enregistre un nouveau patient + crée son dossier médical + responsable légal si mineur. */
    public Patient enregistrer(Patient patient, ResponsableLegal rl) {
        patientRepo.save(patient);
        // Créer dossier médical
        DossierMedical dm = new DossierMedical();
        dm.setIdPatient(patient.getIdPatient());
        dm.setNumeroRef("DOS-" + LocalDate.now().getYear() + "-" + String.format("%03d", patient.getIdPatient()));
        dm.setDateCreation(LocalDate.now());
        dossierRepo.save(dm);
        // Responsable légal si mineur
        if (patient.estMineur() && rl != null && rl.getNom() != null && !rl.getNom().isBlank()) {
            rl.setIdPatient(patient.getIdPatient());
            patientRepo.saveResponsableLegal(rl);
        }
        return patient;
    }

    public boolean modifier(Patient patient, ResponsableLegal rl) {
        boolean ok = patientRepo.update(patient);
        if (patient.estMineur() && rl != null && rl.getNom() != null && !rl.getNom().isBlank()) {
            rl.setIdPatient(patient.getIdPatient());
            patientRepo.saveResponsableLegal(rl);
        }
        return ok;
    }

    public Optional<Patient> findWithDetails(Long id) {
        Optional<Patient> opt = patientRepo.findById(id);
        opt.ifPresent(p -> {
            patientRepo.findResponsableLegal(id).ifPresent(p::setResponsableLegal);
            dossierRepo.findByPatientId(id).ifPresent(p::setDossierMedical);
        });
        return opt;
    }
}
