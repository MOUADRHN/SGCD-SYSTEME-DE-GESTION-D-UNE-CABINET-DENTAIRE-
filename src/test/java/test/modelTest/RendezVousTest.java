package test.modelTest;

import ma.fst.sgcd.model.RendezVous;
import ma.fst.sgcd.model.enums.MotifRDV;
import ma.fst.sgcd.model.enums.StatutRDV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RendezVous - Tests du modèle")
class RendezVousTest {

    private RendezVous rdv;

    @BeforeEach
    void setUp() {
        rdv = new RendezVous();
        rdv.setIdRDV(1L);
        rdv.setDateHeure(LocalDateTime.of(2025, 6, 15, 10, 30));
        rdv.setMotif(MotifRDV.CONTROLE);
        rdv.setStatut(StatutRDV.PLANIFIE);
        rdv.setDuree(30);
        rdv.setNotes("Première visite");
        rdv.setIdPatient(5L);
        rdv.setIdDentiste(2L);
        rdv.setNomPatient("Benali");
        rdv.setPrenomPatient("Youssef");
        rdv.setNomDentiste("Tazi");
        rdv.setPrenomDentiste("Rachid");
    }

    // ─── Noms complets ───────────────────────────────────────────────────

    @Nested
    @DisplayName("getNomCompletPatient()")
    class NomCompletPatientTest {

        @Test
        @DisplayName("Concatène prénom + nom du patient")
        void nomCompletPatient() {
            assertEquals("Youssef Benali", rdv.getNomCompletPatient());
        }

        @Test
        @DisplayName("Fonctionne avec d'autres valeurs")
        void autreNom() {
            rdv.setPrenomPatient("Fatima");
            rdv.setNomPatient("Alaoui");
            assertEquals("Fatima Alaoui", rdv.getNomCompletPatient());
        }
    }

    @Nested
    @DisplayName("getNomCompletDentiste()")
    class NomCompletDentisteTest {

        @Test
        @DisplayName("Préfixe le nom du dentiste avec 'Dr.'")
        void nomCompletDentiste() {
            assertEquals("Dr. Rachid Tazi", rdv.getNomCompletDentiste());
        }

        @Test
        @DisplayName("Fonctionne avec d'autres valeurs")
        void autreNomDentiste() {
            rdv.setPrenomDentiste("Nadia");
            rdv.setNomDentiste("Chraibi");
            assertEquals("Dr. Nadia Chraibi", rdv.getNomCompletDentiste());
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
                () -> assertEquals(1L, rdv.getIdRDV()),
                () -> assertEquals(LocalDateTime.of(2025, 6, 15, 10, 30), rdv.getDateHeure()),
                () -> assertEquals(MotifRDV.CONTROLE, rdv.getMotif()),
                () -> assertEquals(StatutRDV.PLANIFIE, rdv.getStatut()),
                () -> assertEquals(30, rdv.getDuree()),
                () -> assertEquals("Première visite", rdv.getNotes()),
                () -> assertEquals(5L, rdv.getIdPatient()),
                () -> assertEquals(2L, rdv.getIdDentiste())
            );
        }

        @Test
        @DisplayName("Le statut peut être modifié")
        void changementStatut() {
            rdv.setStatut(StatutRDV.EN_SALLE_ATTENTE);
            assertEquals(StatutRDV.EN_SALLE_ATTENTE, rdv.getStatut());

            rdv.setStatut(StatutRDV.EN_COURS);
            assertEquals(StatutRDV.EN_COURS, rdv.getStatut());

            rdv.setStatut(StatutRDV.TERMINE);
            assertEquals(StatutRDV.TERMINE, rdv.getStatut());

            rdv.setStatut(StatutRDV.ANNULE);
            assertEquals(StatutRDV.ANNULE, rdv.getStatut());
        }

        @Test
        @DisplayName("idAssistante peut être assigné et récupéré")
        void idAssistante() {
            rdv.setIdAssistante(3L);
            assertEquals(3L, rdv.getIdAssistante());
        }
    }
}
