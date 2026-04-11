package test.serviceTest;

import ma.fst.sgcd.model.RendezVous;
import ma.fst.sgcd.model.enums.MotifRDV;
import ma.fst.sgcd.model.enums.StatutRDV;
import ma.fst.sgcd.repository.RendezVousRepository;
import ma.fst.sgcd.service.RendezVousService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RendezVousService - Tests unitaires")
class RendezVousServiceTest {

    @Mock
    private RendezVousRepository repo;

    @InjectMocks
    private RendezVousService service;

    private RendezVous rdv;

    @BeforeEach
    void setUp() {
        rdv = new RendezVous();
        rdv.setIdRDV(1L);
        rdv.setMotif(MotifRDV.CONTROLE);
        rdv.setDuree(30);
        rdv.setIdPatient(5L);
        rdv.setIdDentiste(2L);
        rdv.setDateHeure(LocalDateTime.now().plusDays(1));
    }

    // ─── planifier ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("planifier()")
    class PlanifierTest {

        @Test
        @DisplayName("Définit le statut à PLANIFIE avant la sauvegarde")
        void statutPlanifie() {
            ArgumentCaptor<RendezVous> captor = ArgumentCaptor.forClass(RendezVous.class);
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.planifier(rdv);

            verify(repo).save(captor.capture());
            assertEquals(StatutRDV.PLANIFIE, captor.getValue().getStatut());
        }

        @Test
        @DisplayName("Retourne le RDV sauvegardé")
        void retourneRDV() {
            when(repo.save(rdv)).thenReturn(rdv);
            RendezVous result = service.planifier(rdv);
            assertSame(rdv, result);
        }
    }

    // ─── Transitions de statut ────────────────────────────────────────────

    @Nested
    @DisplayName("Transitions de statut")
    class TransitionsStatutTest {

        @Test
        @DisplayName("annuler() met le statut à ANNULE")
        void annuler() {
            when(repo.updateStatut(1L, StatutRDV.ANNULE)).thenReturn(true);
            assertTrue(service.annuler(1L));
            verify(repo).updateStatut(1L, StatutRDV.ANNULE);
        }

        @Test
        @DisplayName("marquerArrivee() met le statut à EN_SALLE_ATTENTE")
        void marquerArrivee() {
            when(repo.updateStatut(1L, StatutRDV.EN_SALLE_ATTENTE)).thenReturn(true);
            assertTrue(service.marquerArrivee(1L));
            verify(repo).updateStatut(1L, StatutRDV.EN_SALLE_ATTENTE);
        }

        @Test
        @DisplayName("marquerEnCours() met le statut à EN_COURS")
        void marquerEnCours() {
            when(repo.updateStatut(1L, StatutRDV.EN_COURS)).thenReturn(true);
            assertTrue(service.marquerEnCours(1L));
            verify(repo).updateStatut(1L, StatutRDV.EN_COURS);
        }

        @Test
        @DisplayName("marquerTermine() met le statut à TERMINE")
        void marquerTermine() {
            when(repo.updateStatut(1L, StatutRDV.TERMINE)).thenReturn(true);
            assertTrue(service.marquerTermine(1L));
            verify(repo).updateStatut(1L, StatutRDV.TERMINE);
        }

        @Test
        @DisplayName("Retourne false si l'ID n'existe pas")
        void idInexistant() {
            when(repo.updateStatut(99L, StatutRDV.ANNULE)).thenReturn(false);
            assertFalse(service.annuler(99L));
        }
    }

    // ─── Recherches ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Méthodes de recherche")
    class RechercheTest {

        @Test
        @DisplayName("findAll() délègue au repository")
        void findAll() {
            when(repo.findAll()).thenReturn(List.of(rdv));
            assertEquals(1, service.findAll().size());
        }

        @Test
        @DisplayName("findById() retourne le RDV existant")
        void findById() {
            when(repo.findById(1L)).thenReturn(Optional.of(rdv));
            assertTrue(service.findById(1L).isPresent());
        }

        @Test
        @DisplayName("findToday() délègue avec la date du jour")
        void findToday() {
            when(repo.findByDate(LocalDate.now())).thenReturn(List.of(rdv));
            List<RendezVous> result = service.findToday();
            assertEquals(1, result.size());
            verify(repo).findByDate(LocalDate.now());
        }

        @Test
        @DisplayName("findByPatient() délègue avec l'ID patient")
        void findByPatient() {
            when(repo.findByPatient(5L)).thenReturn(List.of(rdv));
            assertEquals(1, service.findByPatient(5L).size());
        }

        @Test
        @DisplayName("countToday() retourne le nombre du repository")
        void countToday() {
            when(repo.countToday()).thenReturn(7);
            assertEquals(7, service.countToday());
        }

        @Test
        @DisplayName("findByWeek() transmet les bornes correctement")
        void findByWeek() {
            LocalDate start = LocalDate.now();
            LocalDate end   = start.plusDays(6);
            when(repo.findByWeek(start, end)).thenReturn(List.of(rdv));
            assertEquals(1, service.findByWeek(start, end).size());
        }
    }

    // ─── modifier ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("modifier()")
    class ModifierTest {

        @Test
        @DisplayName("Retourne true si la modification réussit")
        void modifierReussit() {
            when(repo.update(rdv)).thenReturn(true);
            assertTrue(service.modifier(rdv));
        }

        @Test
        @DisplayName("Retourne false si la modification échoue")
        void modifierEchoue() {
            when(repo.update(rdv)).thenReturn(false);
            assertFalse(service.modifier(rdv));
        }
    }
}
