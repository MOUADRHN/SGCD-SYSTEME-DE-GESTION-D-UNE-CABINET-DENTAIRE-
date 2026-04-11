package test.modelTest;

import ma.fst.sgcd.model.Acte;
import ma.fst.sgcd.model.Consultation;
import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Prescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Consultation - Tests du modèle")
class ConsultationTest {

    private Consultation consultation;

    @BeforeEach
    void setUp() {
        consultation = new Consultation();
        consultation.setIdConsultation(1L);
        consultation.setDate(LocalDate.now());
        consultation.setDiagnostic("Carie dentaire");
        consultation.setObservations("Douleur modérée à la dent 36");
        consultation.setIdRDV(10L);
        consultation.setIdDossier(5L);
        consultation.setIdDentiste(2L);
        consultation.setNomDentiste("Tazi");
        consultation.setPrenomDentiste("Rachid");
        consultation.setNomPatient("Benali");
        consultation.setPrenomPatient("Youssef");
    }

    // ─── calculerMontantTotal ─────────────────────────────────────────────

    @Nested
    @DisplayName("calculerMontantTotal()")
    class CalculMontantTest {

        @Test
        @DisplayName("Retourne 0.0 quand la liste d'actes est vide")
        void listVide() {
            consultation.setActes(Collections.emptyList());
            assertEquals(0.0, consultation.calculerMontantTotal(), 0.001);
        }

        @Test
        @DisplayName("Calcule correctement la somme d'un seul acte")
        void unActe() {
            Acte acte = new Acte("D001", "Détartrage", 250.0);
            consultation.setActes(List.of(acte));
            assertEquals(250.0, consultation.calculerMontantTotal(), 0.001);
        }

        @Test
        @DisplayName("Calcule correctement la somme de plusieurs actes")
        void plusieursActes() {
            List<Acte> actes = Arrays.asList(
                new Acte("D001", "Détartrage", 250.0),
                new Acte("S001", "Soin carie", 400.0),
                new Acte("R001", "Radiographie", 150.0)
            );
            consultation.setActes(actes);
            assertEquals(800.0, consultation.calculerMontantTotal(), 0.001);
        }

        @Test
        @DisplayName("Calcule correctement avec des tarifs décimaux")
        void tarifDecimaux() {
            List<Acte> actes = Arrays.asList(
                new Acte("A1", "Acte A", 99.99),
                new Acte("A2", "Acte B", 0.01)
            );
            consultation.setActes(actes);
            assertEquals(100.0, consultation.calculerMontantTotal(), 0.001);
        }
    }

    // ─── Getters / Setters ────────────────────────────────────────────────

    @Nested
    @DisplayName("Getters et Setters")
    class GetterSetterTest {

        @Test
        @DisplayName("Tous les champs sont correctement stockés")
        void champsStockes() {
            assertAll(
                () -> assertEquals(1L, consultation.getIdConsultation()),
                () -> assertEquals(LocalDate.now(), consultation.getDate()),
                () -> assertEquals("Carie dentaire", consultation.getDiagnostic()),
                () -> assertEquals("Douleur modérée à la dent 36", consultation.getObservations()),
                () -> assertEquals(10L, consultation.getIdRDV()),
                () -> assertEquals(5L, consultation.getIdDossier()),
                () -> assertEquals(2L, consultation.getIdDentiste()),
                () -> assertEquals("Tazi", consultation.getNomDentiste()),
                () -> assertEquals("Rachid", consultation.getPrenomDentiste()),
                () -> assertEquals("Benali", consultation.getNomPatient()),
                () -> assertEquals("Youssef", consultation.getPrenomPatient())
            );
        }

        @Test
        @DisplayName("La liste d'actes est initialisée à vide par défaut")
        void actesInitialises() {
            Consultation c = new Consultation();
            assertNotNull(c.getActes());
            assertTrue(c.getActes().isEmpty());
        }

        @Test
        @DisplayName("Les documents médicaux sont initialisés à vide par défaut")
        void documentsInitialises() {
            Consultation c = new Consultation();
            assertNotNull(c.getDocuments());
            assertTrue(c.getDocuments().isEmpty());
        }

        @Test
        @DisplayName("La prescription peut être assignée et récupérée")
        void prescription() {
            Prescription p = new Prescription();
            consultation.setPrescription(p);
            assertNotNull(consultation.getPrescription());
        }

        @Test
        @DisplayName("La facture peut être assignée et récupérée")
        void facture() {
            Facture f = new Facture();
            f.setMontantTotal(500.0);
            consultation.setFacture(f);
            assertNotNull(consultation.getFacture());
            assertEquals(500.0, consultation.getFacture().getMontantTotal());
        }
    }
}
