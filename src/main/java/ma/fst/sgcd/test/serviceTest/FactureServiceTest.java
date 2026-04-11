package ma.fst.sgcd.test.serviceTest;

import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Paiement;
import ma.fst.sgcd.model.enums.StatutFacture;
import ma.fst.sgcd.repository.FactureRepository;
import ma.fst.sgcd.service.FactureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FactureService - Tests unitaires")
class FactureServiceTest {

    @Mock
    private FactureRepository repo;

    @InjectMocks
    private FactureService service;

    private Facture facture;
    private Paiement paiement;

    @BeforeEach
    void setUp() {
        facture = new Facture();
        facture.setIdFacture(1L);
        facture.setDate(LocalDate.now());
        facture.setMontantTotal(650.0);
        facture.setStatut(StatutFacture.EN_ATTENTE);
        facture.setEmailEnvoye(false);
        facture.setIdConsultation(10L);

        paiement = new Paiement();
        paiement.setIdFacture(1L);
        paiement.setMontant(650.0);
    }

    // ─── enregistrerPaiement ──────────────────────────────────────────────

    @Nested
    @DisplayName("enregistrerPaiement()")
    class PaiementTest {

        @Test
        @DisplayName("Sauvegarde le paiement")
        void sauvegardePaiement() {
            when(repo.savePaiement(paiement)).thenReturn(paiement);
            service.enregistrerPaiement(paiement);
            verify(repo).savePaiement(paiement);
        }

        @Test
        @DisplayName("Met à jour le statut de la facture à PAYEE")
        void statutFacturePayee() {
            when(repo.savePaiement(paiement)).thenReturn(paiement);
            service.enregistrerPaiement(paiement);
            verify(repo).updateStatut(1L, StatutFacture.PAYEE);
        }

        @Test
        @DisplayName("Retourne le paiement sauvegardé")
        void retournePaiement() {
            when(repo.savePaiement(paiement)).thenReturn(paiement);
            Paiement result = service.enregistrerPaiement(paiement);
            assertSame(paiement, result);
        }
    }

    // ─── Méthodes de lecture ──────────────────────────────────────────────

    @Nested
    @DisplayName("Méthodes de lecture")
    class LectureTest {

        @Test
        @DisplayName("findAll() retourne toutes les factures")
        void findAll() {
            when(repo.findAll()).thenReturn(List.of(facture));
            assertEquals(1, service.findAll().size());
        }

        @Test
        @DisplayName("findById() retourne la facture existante")
        void findById() {
            when(repo.findById(1L)).thenReturn(Optional.of(facture));
            assertTrue(service.findById(1L).isPresent());
            assertEquals(650.0, service.findById(1L).get().getMontantTotal(), 0.001);
        }

        @Test
        @DisplayName("findById() retourne empty pour un ID inconnu")
        void findByIdInconnu() {
            when(repo.findById(99L)).thenReturn(Optional.empty());
            assertFalse(service.findById(99L).isPresent());
        }

        @Test
        @DisplayName("findByConsultation() retourne la facture liée")
        void findByConsultation() {
            when(repo.findByConsultation(10L)).thenReturn(Optional.of(facture));
            Optional<Facture> result = service.findByConsultation(10L);
            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getIdFacture());
        }

        @Test
        @DisplayName("chiffreAffairesMois() retourne le total du repository")
        void chiffreAffairesMois() {
            when(repo.chiffreAffairesMois()).thenReturn(12500.0);
            assertEquals(12500.0, service.chiffreAffairesMois(), 0.001);
        }
    }

    // ─── Facture model ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Facture - calculerMontant()")
    class FactureModelTest {

        @Test
        @DisplayName("calculerMontant() retourne le montantTotal")
        void calculerMontant() {
            assertEquals(650.0, facture.calculerMontant(), 0.001);
        }

        @Test
        @DisplayName("calculerMontant() retourne 0 pour une facture vide")
        void calculerMontantZero() {
            Facture f = new Facture();
            f.setMontantTotal(0.0);
            assertEquals(0.0, f.calculerMontant(), 0.001);
        }
    }
}
