package ma.fst.sgcd.service;

import ma.fst.sgcd.model.*;
import ma.fst.sgcd.model.enums.StatutFacture;
import ma.fst.sgcd.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ConsultationService {
    private final ConsultationRepository  consultationRepo;
    private final FactureRepository       factureRepo;
    private final PrescriptionRepository  prescriptionRepo;
    private final ActeRepository          acteRepo;
    private final DossierMedicalRepository dossierRepo;

    public ConsultationService(ConsultationRepository cr, FactureRepository fr,
                               PrescriptionRepository pr, ActeRepository ar,
                               DossierMedicalRepository dr) {
        this.consultationRepo  = cr;
        this.factureRepo       = fr;
        this.prescriptionRepo  = pr;
        this.acteRepo          = ar;
        this.dossierRepo       = dr;
    }

    public Optional<Consultation> findById(Long id)         { return consultationRepo.findById(id); }
    public List<Consultation> findByDossier(Long idDossier) { return consultationRepo.findByDossier(idDossier); }
    public Optional<Consultation> findByRdv(Long idRdv)     { return consultationRepo.findByRdv(idRdv); }
    public int countThisMonth()                             { return consultationRepo.countThisMonth(); }
    public List<Acte> findAllActes()                        { return acteRepo.findAll(); }

    /** Ouvre une consultation, génère la facture en attente. */
    public Consultation ouvrir(Consultation consultation) {
        consultationRepo.save(consultation);
        // Facture automatique EN_ATTENTE
        Facture f = new Facture();
        f.setDate(LocalDate.now());
        f.setMontantTotal(consultation.calculerMontantTotal());
        f.setStatut(StatutFacture.EN_ATTENTE);
        f.setEmailEnvoye(false);
        f.setIdConsultation(consultation.getIdConsultation());
        factureRepo.save(f);
        return consultation;
    }

    public Optional<Prescription> findPrescription(Long idConsultation) {
        return prescriptionRepo.findByConsultation(idConsultation);
    }

    public Prescription prescrire(Prescription prescription) {
        return prescriptionRepo.save(prescription);
    }
}
