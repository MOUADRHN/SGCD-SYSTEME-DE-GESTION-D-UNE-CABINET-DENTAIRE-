package test.serviceTest;

import ma.fst.sgcd.model.Acte;
import ma.fst.sgcd.model.Consultation;
import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Prescription;
import ma.fst.sgcd.model.enums.StatutFacture;
import ma.fst.sgcd.repository.*;
import ma.fst.sgcd.service.ConsultationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultationService - Tests unitaires")
class ConsultationServiceTest {

    @Mock private ConsultationRepository  consultationRepo;
    @Mock private FactureRepository       factureRepo;
    @Mock private PrescriptionRepository  prescriptionRepo;
    @Mock private ActeRepository          acteRepo;
    @Mock private DossierMedicalRepository dossierRepo;

    @InjectMocks
    private ConsultationService service;

    private Consultation consultation;

    @BeforeEach
    void setUp() {
        consultation = new Consultation();
        consultation.setIdConsultation(1L);
        consultation.setDate(LocalDate.now());
        consultation.setDiagnostic("Carie sur dent 36");
        consultation.setIdRDV(10L);
        consultation.setIdDossier(5L);
        consultation.setIdDentiste(2L);
    }

    // ─── ouvrir ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("ouvrir()")
    class OuvrirTest {

        @Test
        @DisplayName("Sauvegarde la consultation")
        void sauvegardeConsultation() {
            service.ouvrir(consultation);
            verify(consultationRepo).save(consultation);
        }

        @Test
        @DisplayName("Génère une facture EN_ATTENTE automatiquement")
        void genereFactureEnAttente() {
            ArgumentCaptor<Facture> captor = ArgumentCaptor.forClass(Facture.class);
            service.ouvrir(consultation);

            verify(factureRepo).save(captor.capture());
            Facture facture = captor.getValue();
            assertEquals(StatutFacture.EN_ATTENTE, facture.getStatut());
        }

        @Test
        @DisplayName("La facture n'a pas encore été envoyée par email")
        void emailNonEnvoye() {
            ArgumentCaptor<Facture> captor = ArgumentCaptor.forClass(Facture.class);
            service.ouvrir(consultation);

            verify(factureRepo).save(captor.capture());
            assertFalse(captor.getValue().isEmailEnvoye());
        }

        @Test
        @DisplayName("La facture est liée à la consultation")
        void factureLieeConsultation() {
            ArgumentCaptor<Facture> captor = ArgumentCaptor.forClass(Facture.class);
            service.ouvrir(consultation);

            verify(factureRepo).save(captor.capture());
            assertEquals(consultation.getIdConsultation(), captor.getValue().getIdConsultation());
        }

        @Test
        @DisplayName("Le montant de la facture correspond au total des actes")
        void montantFactureCalcule() {
            List<Acte> actes = Arrays.asList(
                new Acte("D001", "Détartrage", 250.0),
                new Acte("S001", "Soin carie", 400.0)
            );
            consultation.setActes(actes);

            ArgumentCaptor<Facture> captor = ArgumentCaptor.forClass(Facture.class);
            service.ouvrir(consultation);

            verify(factureRepo).save(captor.capture());
            assertEquals(650.0, captor.getValue().getMontantTotal(), 0.001);
        }

        @Test
        @DisplayName("La date de la facture est aujourd'hui")
        void dateFActureAujourdhui() {
            ArgumentCaptor<Facture> captor = ArgumentCaptor.forClass(Facture.class);
            service.ouvrir(consultation);

            verify(factureRepo).save(captor.capture());
            assertEquals(LocalDate.now(), captor.getValue().getDate());
        }

        @Test
        @DisplayName("Retourne la consultation sauvegardée")
        void retourneConsultation() {
            Consultation result = service.ouvrir(consultation);
            assertSame(consultation, result);
        }
    }

    // ─── Prescriptions ────────────────────────────────────────────────────

    @Nested
    @DisplayName("prescrire() et findPrescription()")
    class PrescriptionTest {

        @Test
        @DisplayName("prescrire() délègue au repository de prescriptions")
        void prescrire() {
            Prescription p = new Prescription();
            p.setIdConsultation(1L);
            when(prescriptionRepo.save(p)).thenReturn(p);

            Prescription result = service.prescrire(p);
            assertSame(p, result);
            verify(prescriptionRepo).save(p);
        }

        @Test
        @DisplayName("findPrescription() retourne la prescription si elle existe")
        void findPrescriptionExistante() {
            Prescription p = new Prescription();
            when(prescriptionRepo.findByConsultation(1L)).thenReturn(Optional.of(p));

            Optional<Prescription> result = service.findPrescription(1L);
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("findPrescription() retourne empty si aucune prescription")
        void findPrescriptionAbsente() {
            when(prescriptionRepo.findByConsultation(99L)).thenReturn(Optional.empty());
            assertFalse(service.findPrescription(99L).isPresent());
        }
    }

    // ─── Méthodes de lecture ──────────────────────────────────────────────

    @Nested
    @DisplayName("Méthodes de lecture")
    class LectureTest {

        @Test
        @DisplayName("findById() retourne la consultation correspondante")
        void findById() {
            when(consultationRepo.findById(1L)).thenReturn(Optional.of(consultation));
            assertTrue(service.findById(1L).isPresent());
        }

        @Test
        @DisplayName("findByDossier() retourne toutes les consultations du dossier")
        void findByDossier() {
            when(consultationRepo.findByDossier(5L)).thenReturn(List.of(consultation));
            assertEquals(1, service.findByDossier(5L).size());
        }

        @Test
        @DisplayName("findByRdv() retourne la consultation liée au RDV")
        void findByRdv() {
            when(consultationRepo.findByRdv(10L)).thenReturn(Optional.of(consultation));
            assertTrue(service.findByRdv(10L).isPresent());
        }

        @Test
        @DisplayName("countThisMonth() retourne le compte du mois en cours")
        void countThisMonth() {
            when(consultationRepo.countThisMonth()).thenReturn(15);
            assertEquals(15, service.countThisMonth());
        }

        @Test
        @DisplayName("findAllActes() retourne tous les actes disponibles")
        void findAllActes() {
            List<Acte> actes = List.of(new Acte("D001", "Détartrage", 250.0));
            when(acteRepo.findAll()).thenReturn(actes);
            assertEquals(1, service.findAllActes().size());
        }
    }
}
