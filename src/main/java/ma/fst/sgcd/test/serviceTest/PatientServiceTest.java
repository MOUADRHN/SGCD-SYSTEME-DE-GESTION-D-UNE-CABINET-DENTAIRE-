package ma.fst.sgcd.test.serviceTest;

import ma.fst.sgcd.model.DossierMedical;
import ma.fst.sgcd.model.Patient;
import ma.fst.sgcd.model.ResponsableLegal;
import ma.fst.sgcd.model.enums.Sexe;
import ma.fst.sgcd.repository.DossierMedicalRepository;
import ma.fst.sgcd.repository.PatientRepository;
import ma.fst.sgcd.service.PatientService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService - Tests unitaires")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepo;

    @Mock
    private DossierMedicalRepository dossierRepo;

    @InjectMocks
    private PatientService patientService;

    private Patient patientAdulte;
    private Patient patientMineur;

    @BeforeEach
    void setUp() {
        patientAdulte = new Patient();
        patientAdulte.setIdPatient(1L);
        patientAdulte.setNom("Benali");
        patientAdulte.setPrenom("Youssef");
        patientAdulte.setDateNaissance(LocalDate.now().minusYears(30));
        patientAdulte.setSexe(Sexe.H);

        patientMineur = new Patient();
        patientMineur.setIdPatient(2L);
        patientMineur.setNom("Fassi");
        patientMineur.setPrenom("Amine");
        patientMineur.setDateNaissance(LocalDate.now().minusYears(10));
        patientMineur.setSexe(Sexe.H);
    }

    // ─── findAll / findById / search / countAll ───────────────────────────

    @Nested
    @DisplayName("Méthodes de lecture")
    class LectureTest {

        @Test
        @DisplayName("findAll() délègue au repository")
        void findAll() {
            when(patientRepo.findAll()).thenReturn(List.of(patientAdulte, patientMineur));
            List<Patient> result = patientService.findAll();
            assertEquals(2, result.size());
            verify(patientRepo).findAll();
        }

        @Test
        @DisplayName("findById() retourne le patient correspondant")
        void findById() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patientAdulte));
            Optional<Patient> result = patientService.findById(1L);
            assertTrue(result.isPresent());
            assertEquals("Benali", result.get().getNom());
        }

        @Test
        @DisplayName("findById() retourne empty pour un ID inconnu")
        void findByIdInconnu() {
            when(patientRepo.findById(99L)).thenReturn(Optional.empty());
            assertFalse(patientService.findById(99L).isPresent());
        }

        @Test
        @DisplayName("search() délègue au repository avec le bon terme")
        void search() {
            when(patientRepo.search("Benali")).thenReturn(List.of(patientAdulte));
            List<Patient> result = patientService.search("Benali");
            assertEquals(1, result.size());
            verify(patientRepo).search("Benali");
        }

        @Test
        @DisplayName("countAll() retourne le compte du repository")
        void countAll() {
            when(patientRepo.countAll()).thenReturn(42);
            assertEquals(42, patientService.countAll());
        }
    }

    // ─── enregistrer ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("enregistrer()")
    class EnregistrerTest {

        @Test
        @DisplayName("Sauvegarde le patient et crée un dossier médical")
        void creerPatientEtDossier() {
            patientService.enregistrer(patientAdulte, null);

            verify(patientRepo).save(patientAdulte);
            verify(dossierRepo).save(any(DossierMedical.class));
        }

        @Test
        @DisplayName("Le numéro de référence du dossier suit le format DOS-ANNÉE-XXX")
        void formatNumeroRef() {
            ArgumentCaptor<DossierMedical> captor = ArgumentCaptor.forClass(DossierMedical.class);
            patientService.enregistrer(patientAdulte, null);

            verify(dossierRepo).save(captor.capture());
            String ref = captor.getValue().getNumeroRef();
            assertTrue(ref.startsWith("DOS-"));
            assertTrue(ref.contains(String.valueOf(LocalDate.now().getYear())));
        }

        @Test
        @DisplayName("Crée le responsable légal pour un patient mineur avec RL fourni")
        void responsableLegalPourMineur() {
            ResponsableLegal rl = new ResponsableLegal();
            rl.setNom("Fassi");


            patientService.enregistrer(patientMineur, rl);

            verify(patientRepo).saveResponsableLegal(rl);
            assertEquals(patientMineur.getIdPatient(), rl.getIdPatient());
        }

        @Test
        @DisplayName("Ne crée pas de RL pour un adulte même si RL est fourni")
        void pasDeRLPourAdulte() {
            ResponsableLegal rl = new ResponsableLegal();
            rl.setNom("Benali");


            patientService.enregistrer(patientAdulte, rl);

            verify(patientRepo, never()).saveResponsableLegal(any());
        }

        @Test
        @DisplayName("Ne crée pas de RL pour un mineur si RL est null")
        void rlNullPourMineur() {
            patientService.enregistrer(patientMineur, null);
            verify(patientRepo, never()).saveResponsableLegal(any());
        }

        @Test
        @DisplayName("Ne crée pas de RL pour un mineur si le nom du RL est vide")
        void rlNomVidePourMineur() {
            ResponsableLegal rl = new ResponsableLegal();
            rl.setNom("  "); // blank

            patientService.enregistrer(patientMineur, rl);

            verify(patientRepo, never()).saveResponsableLegal(any());
        }

        @Test
        @DisplayName("Retourne le patient enregistré")
        void retournePatient() {
            Patient result = patientService.enregistrer(patientAdulte, null);
            assertSame(patientAdulte, result);
        }
    }

    // ─── modifier ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("modifier()")
    class ModifierTest {

        @Test
        @DisplayName("Retourne true si la mise à jour est réussie")
        void modifierSuccess() {
            when(patientRepo.update(patientAdulte)).thenReturn(true);
            assertTrue(patientService.modifier(patientAdulte, null));
        }

        @Test
        @DisplayName("Retourne false si la mise à jour échoue")
        void modifierEchec() {
            when(patientRepo.update(patientAdulte)).thenReturn(false);
            assertFalse(patientService.modifier(patientAdulte, null));
        }

        @Test
        @DisplayName("Enregistre le RL lors de la modification d'un mineur")
        void modifierMineurAvecRL() {
            when(patientRepo.update(patientMineur)).thenReturn(true);
            ResponsableLegal rl = new ResponsableLegal();
            rl.setNom("Fassi");

            patientService.modifier(patientMineur, rl);

            verify(patientRepo).saveResponsableLegal(rl);
        }
    }

    // ─── findWithDetails ──────────────────────────────────────────────────

    @Nested
    @DisplayName("findWithDetails()")
    class FindWithDetailsTest {

        @Test
        @DisplayName("Charge le responsable légal et le dossier médical")
        void chargeDetails() {
            ResponsableLegal rl = new ResponsableLegal();
            DossierMedical dm   = new DossierMedical();

            when(patientRepo.findById(1L)).thenReturn(Optional.of(patientAdulte));
            when(patientRepo.findResponsableLegal(1L)).thenReturn(Optional.of(rl));
            when(dossierRepo.findByPatientId(1L)).thenReturn(Optional.of(dm));

            Optional<Patient> result = patientService.findWithDetails(1L);

            assertTrue(result.isPresent());
            assertSame(rl, result.get().getResponsableLegal());
            assertSame(dm, result.get().getDossierMedical());
        }

        @Test
        @DisplayName("Retourne empty si le patient n'existe pas")
        void patientInexistant() {
            when(patientRepo.findById(99L)).thenReturn(Optional.empty());
            assertFalse(patientService.findWithDetails(99L).isPresent());
        }
    }
}
