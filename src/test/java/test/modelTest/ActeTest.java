package test.modelTest;

import ma.fst.sgcd.model.Acte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Acte - Tests du modèle")
class ActeTest {

    @Nested
    @DisplayName("Constructeur avec paramètres")
    class ConstructeurTest {

        @Test
        @DisplayName("Initialise correctement code, nom et tarif")
        void constructeurComplet() {
            Acte acte = new Acte("D001", "Détartrage simple", 250.0);

            assertAll(
                () -> assertEquals("D001", acte.getCode()),
                () -> assertEquals("Détartrage simple", acte.getNom()),
                () -> assertEquals(250.0, acte.getTarifBase(), 0.001)
            );
        }
    }

    @Nested
    @DisplayName("getTarif()")
    class GetTarifTest {

        @Test
        @DisplayName("getTarif() retourne la même valeur que getTarifBase()")
        void getTarifEgalTarifBase() {
            Acte acte = new Acte("S001", "Soin carie", 400.0);
            assertEquals(acte.getTarifBase(), acte.getTarif(), 0.001);
        }

        @Test
        @DisplayName("Tarif à zéro")
        void tarifZero() {
            Acte acte = new Acte("G001", "Consultation gratuite", 0.0);
            assertEquals(0.0, acte.getTarif(), 0.001);
        }
    }

    @Nested
    @DisplayName("Setters")
    class SetterTest {

        @Test
        @DisplayName("Les setters modifient correctement les champs")
        void setters() {
            Acte acte = new Acte();
            acte.setCode("R001");
            acte.setNom("Radiographie");
            acte.setTarifBase(150.0);

            assertAll(
                () -> assertEquals("R001", acte.getCode()),
                () -> assertEquals("Radiographie", acte.getNom()),
                () -> assertEquals(150.0, acte.getTarifBase(), 0.001)
            );
        }
    }
}
