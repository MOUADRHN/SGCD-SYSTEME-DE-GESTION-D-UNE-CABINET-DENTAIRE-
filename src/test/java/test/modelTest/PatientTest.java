package test.modelTest;

import ma.fst.sgcd.model.DossierMedical;
import ma.fst.sgcd.model.Patient;
import ma.fst.sgcd.model.ResponsableLegal;
import ma.fst.sgcd.model.enums.Sexe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Patient - Tests du modèle")
class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setIdPatient(1L);
        patient.setNom("Benali");
        patient.setPrenom("Youssef");
        patient.setSexe(Sexe.H);
        patient.setTelephone("0661234567");
        patient.setAdresse("12 Rue Hassan II, Fès");
    }

    // ─── Nom complet ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("getNomComplet()")
    class NomCompletTest {

        @Test
        @DisplayName("Concatène prénom + nom")
        void retourneNomComplet() {
            assertEquals("Youssef Benali", patient.getNomComplet());
        }

        @Test
        @DisplayName("Fonctionne avec un seul mot pour chaque champ")
        void nomSimple() {
            patient.setPrenom("Ali");
            patient.setNom("Idrissi");
            assertEquals("Ali Idrissi", patient.getNomComplet());
        }
    }

    // ─── Âge & Minorité ──────────────────────────────────────────────────

    @Nested
    @DisplayName("getAge() et estMineur()")
    class AgeMineurTest {

        @Test
        @DisplayName("Calcule correctement l'âge d'un adulte")
        void ageAdulte() {
            patient.setDateNaissance(LocalDate.now().minusYears(30));
            assertEquals(30, patient.getAge());
        }

        @Test
        @DisplayName("Calcule correctement l'âge d'un mineur")
        void ageMineur() {
            patient.setDateNaissance(LocalDate.now().minusYears(10));
            assertEquals(10, patient.getAge());
        }

        @Test
        @DisplayName("estMineur() retourne true pour un patient de 10 ans")
        void estMineurVrai() {
            patient.setDateNaissance(LocalDate.now().minusYears(10));
            assertTrue(patient.estMineur());
        }

        @Test
        @DisplayName("estMineur() retourne false pour un patient de 18 ans")
        void ageLimite18() {
            patient.setDateNaissance(LocalDate.now().minusYears(18));
            assertFalse(patient.estMineur());
        }

        @Test
        @DisplayName("estMineur() retourne false pour un adulte de 35 ans")
        void estMajeur() {
            patient.setDateNaissance(LocalDate.now().minusYears(35));
            assertFalse(patient.estMineur());
        }

        @Test
        @DisplayName("Age calculé tient compte du mois exact (avant anniversaire)")
        void ageAvantAnniversaire() {
            // Né demain il y a 18 ans → encore 17 ans aujourd'hui
            patient.setDateNaissance(LocalDate.now().minusYears(18).plusDays(1));
            assertEquals(17, patient.getAge());
            assertTrue(patient.estMineur());
        }
    }

    // ─── Getters / Setters de base ────────────────────────────────────────

    @Nested
    @DisplayName("Getters et Setters")
    class GetterSetterTest {

        @Test
        @DisplayName("Tous les champs simples sont correctement stockés")
        void champsSimples() {
            patient.setNumeroCNSS("123456789");
            patient.setAntecedents("Diabète");
            patient.setAllergie("Pénicilline");

            assertAll(
                () -> assertEquals(1L, patient.getIdPatient()),
                () -> assertEquals("Benali", patient.getNom()),
                () -> assertEquals("Youssef", patient.getPrenom()),
                () -> assertEquals("0661234567", patient.getTelephone()),
                () -> assertEquals("12 Rue Hassan II, Fès", patient.getAdresse()),
                () -> assertEquals(Sexe.H, patient.getSexe()),
                () -> assertEquals("123456789", patient.getNumeroCNSS()),
                () -> assertEquals("Diabète", patient.getAntecedents()),
                () -> assertEquals("Pénicilline", patient.getAllergie())
            );
        }

        @Test
        @DisplayName("Le responsable légal peut être assigné et récupéré")
        void responsableLegal() {
            ResponsableLegal rl = new ResponsableLegal();
            rl.setNom("Benali");

            patient.setResponsableLegal(rl);

            assertNotNull(patient.getResponsableLegal());
            assertEquals("Benali", patient.getResponsableLegal().getNom());
        }

        @Test
        @DisplayName("Le dossier médical peut être assigné et récupéré")
        void dossierMedical() {
            DossierMedical dm = new DossierMedical();
            dm.setNumeroRef("DOS-2025-001");
            patient.setDossierMedical(dm);

            assertNotNull(patient.getDossierMedical());
            assertEquals("DOS-2025-001", patient.getDossierMedical().getNumeroRef());
        }
    }
}
