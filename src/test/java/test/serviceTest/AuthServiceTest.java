package test.serviceTest;

import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.model.enums.Statut;
import ma.fst.sgcd.repository.UtilisateurRepository;
import ma.fst.sgcd.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests unitaires")
class AuthServiceTest {

    @Mock
    private UtilisateurRepository repo;

    @InjectMocks
    private AuthService authService;

    private Utilisateur utilisateurActif;
    private final String motDePasseClair  = "secret123";
    private final String motDePasseHache  = BCrypt.hashpw("secret123", BCrypt.gensalt());

    @BeforeEach
    void setUp() {
        utilisateurActif = new Utilisateur(
            1L, "Tazi", "Rachid", "rtazi@cabinet.ma",
            "rtazi", motDePasseHache, Role.DENTISTE, Statut.ACTIF
        );
    }

    // ─── Authentification réussie ─────────────────────────────────────────

    @Nested
    @DisplayName("Authentification réussie")
    class AuthReussieTest {

        @Test
        @DisplayName("Retourne l'utilisateur quand login/mot de passe corrects et compte ACTIF")
        void loginCorrect() {
            when(repo.findByLogin("rtazi")).thenReturn(Optional.of(utilisateurActif));

            Optional<Utilisateur> result = authService.authenticate("rtazi", motDePasseClair);

            assertTrue(result.isPresent());
            assertEquals("rtazi", result.get().getLogin());
            verify(repo, times(1)).findByLogin("rtazi");
        }

        @Test
        @DisplayName("L'utilisateur retourné est le bon objet")
        void retourneLeBonUtilisateur() {
            when(repo.findByLogin("rtazi")).thenReturn(Optional.of(utilisateurActif));

            Optional<Utilisateur> result = authService.authenticate("rtazi", motDePasseClair);

            assertTrue(result.isPresent());
            assertEquals(Role.DENTISTE, result.get().getRole());
            assertEquals(Statut.ACTIF, result.get().getStatut());
        }
    }

    // ─── Login inconnu ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Login inexistant")
    class LoginInexistantTest {

        @Test
        @DisplayName("Retourne Optional.empty() si le login n'existe pas")
        void loginInexistant() {
            when(repo.findByLogin("inconnu")).thenReturn(Optional.empty());

            Optional<Utilisateur> result = authService.authenticate("inconnu", "mdp");

            assertFalse(result.isPresent());
        }
    }

    // ─── Mauvais mot de passe ─────────────────────────────────────────────

    @Nested
    @DisplayName("Mot de passe incorrect")
    class MauvaisMdpTest {

        @Test
        @DisplayName("Retourne Optional.empty() si le mot de passe est incorrect")
        void mauvaisMotDePasse() {
            when(repo.findByLogin("rtazi")).thenReturn(Optional.of(utilisateurActif));

            Optional<Utilisateur> result = authService.authenticate("rtazi", "mauvaisMDP");

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Retourne Optional.empty() pour un mot de passe vide")
        void motDePasseVide() {
            when(repo.findByLogin("rtazi")).thenReturn(Optional.of(utilisateurActif));

            Optional<Utilisateur> result = authService.authenticate("rtazi", "");

            assertFalse(result.isPresent());
        }
    }

    // ─── Compte inactif ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Compte inactif")
    class CompteInactifTest {

        @Test
        @DisplayName("Retourne Optional.empty() si le compte est INACTIF")
        void compteInactif() {
            Utilisateur inactif = new Utilisateur(
                2L, "Ziani", "Omar", "oziani@cabinet.ma",
                "oziani", motDePasseHache, Role.ASSISTANTE, Statut.INACTIF
            );
            when(repo.findByLogin("oziani")).thenReturn(Optional.of(inactif));

            Optional<Utilisateur> result = authService.authenticate("oziani", motDePasseClair);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Un compte INACTIF est refusé même avec le bon mot de passe")
        void compteInactifBonMdp() {
            utilisateurActif.setStatut(Statut.INACTIF);
            when(repo.findByLogin("rtazi")).thenReturn(Optional.of(utilisateurActif));

            Optional<Utilisateur> result = authService.authenticate("rtazi", motDePasseClair);

            assertFalse(result.isPresent());
        }
    }
}
