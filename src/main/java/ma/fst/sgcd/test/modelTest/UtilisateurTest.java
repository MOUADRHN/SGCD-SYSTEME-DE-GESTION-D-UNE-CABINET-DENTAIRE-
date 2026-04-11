package ma.fst.sgcd.test.modelTest;

import ma.fst.sgcd.model.Utilisateur;
import ma.fst.sgcd.model.enums.Role;
import ma.fst.sgcd.model.enums.Statut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Utilisateur - Tests du modèle")
class UtilisateurTest {

    @Nested
    @DisplayName("getNomComplet()")
    class NomCompletTest {

        @Test
        @DisplayName("Concatène prénom + nom")
        void nomComplet() {
            Utilisateur u = new Utilisateur(1L, "Tazi", "Rachid", "rtazi@cabinet.ma",
                    "rtazi", "hash", Role.DENTISTE, Statut.ACTIF);
            assertEquals("Rachid Tazi", u.getNomComplet());
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTest {

        @Test
        @DisplayName("Contient l'id, le login et le rôle")
        void toStringContientInfosClés() {
            Utilisateur u = new Utilisateur(5L, "Admin", "Super", "admin@cabinet.ma",
                    "admin", "hash", Role.ADMINISTRATEUR, Statut.ACTIF);
            String str = u.toString();
            assertTrue(str.contains("5"));
            assertTrue(str.contains("admin"));
            assertTrue(str.contains("ADMINISTRATEUR"));
        }
    }

    @Nested
    @DisplayName("Getters et Setters")
    class GetterSetterTest {

        @Test
        @DisplayName("Constructeur sans argument puis setters")
        void constructeurVide() {
            Utilisateur u = new Utilisateur();
            u.setIdUtilisateur(10L);
            u.setNom("Chraibi");
            u.setPrenom("Nadia");
            u.setEmail("nadia@cabinet.ma");
            u.setLogin("nchraibi");
            u.setMotDePasse("$2a$hash");
            u.setRole(Role.ASSISTANTE);
            u.setStatut(Statut.ACTIF);

            assertAll(
                () -> assertEquals(10L, u.getIdUtilisateur()),
                () -> assertEquals("Chraibi", u.getNom()),
                () -> assertEquals("Nadia", u.getPrenom()),
                () -> assertEquals("nadia@cabinet.ma", u.getEmail()),
                () -> assertEquals("nchraibi", u.getLogin()),
                () -> assertEquals("$2a$hash", u.getMotDePasse()),
                () -> assertEquals(Role.ASSISTANTE, u.getRole()),
                () -> assertEquals(Statut.ACTIF, u.getStatut())
            );
        }
    }
}
