package ma.fst.sgcd.test.modelTest;

import ma.fst.sgcd.model.DossierMedical;
import ma.fst.sgcd.model.Paiement;
import ma.fst.sgcd.model.ResponsableLegal;
import ma.fst.sgcd.model.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Enums et modèles secondaires - Tests")
class EnumsEtModelsTest {

    // ─── StatutRDV ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("StatutRDV")
    class StatutRDVTest {

        @Test
        @DisplayName("Tous les statuts ont un libellé et une couleur de badge")
        void statutsComplets() {
            for (StatutRDV s : StatutRDV.values()) {
                assertNotNull(s.getLibelle(),    "Libellé null pour " + s);
                assertFalse(s.getLibelle().isBlank(), "Libellé vide pour " + s);
                assertNotNull(s.getBadgeColor(), "BadgeColor null pour " + s);
            }
        }

        @Test
        @DisplayName("PLANIFIE a le badge 'secondary'")
        void planifieBadge() {
            assertEquals("secondary", StatutRDV.PLANIFIE.getBadgeColor());
        }

        @Test
        @DisplayName("ANNULE a le badge 'danger'")
        void annuleBadge() {
            assertEquals("danger", StatutRDV.ANNULE.getBadgeColor());
        }

        @Test
        @DisplayName("TERMINE a le badge 'success'")
        void termineBadge() {
            assertEquals("success", StatutRDV.TERMINE.getBadgeColor());
        }

        @Test
        @DisplayName("Il y a exactement 6 statuts de RDV")
        void nombreStatuts() {
            assertEquals(6, StatutRDV.values().length);
        }
    }

    // ─── StatutFacture ────────────────────────────────────────────────────

    @Nested
    @DisplayName("StatutFacture")
    class StatutFactureTest {

        @Test
        @DisplayName("Tous les statuts de facture ont un libellé et une couleur")
        void statutsComplets() {
            for (StatutFacture s : StatutFacture.values()) {
                assertNotNull(s.getLibelle());
                assertNotNull(s.getBadgeColor());
            }
        }

        @Test
        @DisplayName("EN_ATTENTE a le badge 'warning'")
        void enAttenteBadge() {
            assertEquals("warning", StatutFacture.EN_ATTENTE.getBadgeColor());
        }

        @Test
        @DisplayName("PAYEE a le badge 'success'")
        void payeeBadge() {
            assertEquals("success", StatutFacture.PAYEE.getBadgeColor());
        }

        @Test
        @DisplayName("Il y a exactement 3 statuts de facture")
        void nombreStatuts() {
            assertEquals(3, StatutFacture.values().length);
        }
    }

    // ─── Role ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Role")
    class RoleTest {

        @Test
        @DisplayName("Tous les rôles ont un libellé non vide")
        void roleLibelles() {
            for (Role r : Role.values()) {
                assertNotNull(r.getLibelle());
                assertFalse(r.getLibelle().isBlank());
            }
        }

        @Test
        @DisplayName("Il y a exactement 3 rôles")
        void nombreRoles() {
            assertEquals(3, Role.values().length);
        }
    }

    // ─── Statut ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Statut")
    class StatutTest {

        @Test
        @DisplayName("ACTIF et INACTIF ont des libellés")
        void libelles() {
            assertEquals("Actif",   Statut.ACTIF.getLibelle());
            assertEquals("Inactif", Statut.INACTIF.getLibelle());
        }

        @Test
        @DisplayName("Il y a exactement 2 statuts utilisateur")
        void nombreStatuts() {
            assertEquals(2, Statut.values().length);
        }
    }

    // ─── MotifRDV ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MotifRDV")
    class MotifRDVTest {

        @Test
        @DisplayName("Tous les motifs ont un libellé non vide")
        void libelles() {
            for (MotifRDV m : MotifRDV.values()) {
                assertNotNull(m.getLibelle());
                assertFalse(m.getLibelle().isBlank());
            }
        }

        @Test
        @DisplayName("Il y a exactement 6 motifs")
        void nombreMotifs() {
            assertEquals(6, MotifRDV.values().length);
        }
    }

    // ─── ModePaiement ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("ModePaiement")
    class ModePaiementTest {

        @Test
        @DisplayName("Tous les modes ont un libellé")
        void libelles() {
            for (ModePaiement mp : ModePaiement.values()) {
                assertNotNull(mp.getLibelle());
                assertFalse(mp.getLibelle().isBlank());
            }
        }

        @Test
        @DisplayName("Il y a exactement 3 modes de paiement")
        void nombreModes() {
            assertEquals(3, ModePaiement.values().length);
        }
    }

    // ─── Sexe ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Sexe")
    class SexeTest {

        @Test
        @DisplayName("H = Homme, F = Femme")
        void libelles() {
            assertEquals("Homme", Sexe.H.getLibelle());
            assertEquals("Femme", Sexe.F.getLibelle());
        }
    }

    // ─── Paiement ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Paiement - modèle")
    class PaiementModelTest {

        @Test
        @DisplayName("Tous les champs sont correctement stockés")
        void champsStockes() {
            Paiement p = new Paiement();
            p.setIdPaiement(1L);
            p.setIdFacture(10L);
            p.setMontant(500.0);
            p.setModePaiement(ModePaiement.ESPECES);
            p.setDatePaiement(LocalDate.of(2025, 6, 15));

            assertAll(
                () -> assertEquals(1L, p.getIdPaiement()),
                () -> assertEquals(10L, p.getIdFacture()),
                () -> assertEquals(500.0, p.getMontant(), 0.001),
                () -> assertEquals(ModePaiement.ESPECES, p.getModePaiement()),
                () -> assertEquals(LocalDate.of(2025, 6, 15), p.getDatePaiement())
            );
        }
    }

    // ─── DossierMedical ───────────────────────────────────────────────────

    @Nested
    @DisplayName("DossierMedical - modèle")
    class DossierMedicalModelTest {

        @Test
        @DisplayName("Tous les champs sont correctement stockés")
        void champsStockes() {
            DossierMedical dm = new DossierMedical();
            dm.setIdDossier(1L);
            dm.setNumeroRef("DOS-2025-001");
            dm.setDateCreation(LocalDate.of(2025, 1, 10));
            dm.setIdPatient(5L);

            assertAll(
                () -> assertEquals(1L, dm.getIdDossier()),
                () -> assertEquals("DOS-2025-001", dm.getNumeroRef()),
                () -> assertEquals(LocalDate.of(2025, 1, 10), dm.getDateCreation()),
                () -> assertEquals(5L, dm.getIdPatient())
            );
        }

        @Test
        @DisplayName("La liste de consultations est initialisée vide")
        void consultationsInitialisees() {
            DossierMedical dm = new DossierMedical();
            assertNotNull(dm.getConsultations());
            assertTrue(dm.getConsultations().isEmpty());
        }
    }

    // ─── ResponsableLegal ─────────────────────────────────────────────────

    @Nested
    @DisplayName("ResponsableLegal - modèle")
    class ResponsableLegalModelTest {

        @Test
        @DisplayName("Tous les champs sont correctement stockés")
        void champsStockes() {
            ResponsableLegal rl = new ResponsableLegal();
            rl.setIdPatient(2L);
            rl.setNom("Benali");
            rl.setTelephone("0661234567");
            rl.setLienParente("Père");

            assertAll(
                () -> assertEquals(2L, rl.getIdPatient()),
                () -> assertEquals("Benali", rl.getNom()),
                () -> assertEquals("0661234567", rl.getTelephone()),
                () -> assertEquals("Père", rl.getLienParente())
            );
        }
    }
}
