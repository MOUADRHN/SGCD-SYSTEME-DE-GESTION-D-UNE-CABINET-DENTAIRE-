package ma.fst.sgcd;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SGCD — Test de connexion à la base de données MySQL         ║
 * ║  FST Fès — Licence Génie Informatique — 2025-2026            ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * UTILISATION :
 *   1. Adapter les constantes DB_URL, DB_USER, DB_PASSWORD ci-dessous
 *   2. Compiler :
 *      javac -cp mysql-connector-j-8.0.33.jar TestConnexion.java
 *   3. Exécuter :
 *      java  -cp ".;mysql-connector-j-8.0.33.jar" ma.fst.sgcd.test.TestConnexion
 *      (Linux/Mac → remplacer ";" par ":")
 *
 *   OU directement depuis IntelliJ IDEA → Run 'TestConnexion.main()'
 */
public class TestConnexion {

    // ─── PARAMÈTRES — À MODIFIER SELON VOTRE ENVIRONNEMENT ─────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/sgcd"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Africa/Casablanca"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true";

    private static final String DB_USER     = "root";      // ← votre user MySQL
    private static final String DB_PASSWORD = "root";           // ← votre mot de passe MySQL
    // ─────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        printBanner();

        System.out.println("  URL      : " + DB_URL);
        System.out.println("  User     : " + DB_USER);
        System.out.println("  Password : " + (DB_PASSWORD.isEmpty() ? "(vide)" : "***"));
        System.out.println();

        // ── Test 1 : Connexion ───────────────────────────────────────────
        testConnexion();

        // ── Test 2 : Version MySQL ───────────────────────────────────────
        testVersionMySQL();

        // ── Test 3 : Existence des tables ───────────────────────────────
        testTables();

        // ── Test 4 : Données de test ─────────────────────────────────────
        testDonnees();

        // ── Test 5 : Requête métier — agenda du jour ────────────────────
        testAgendaDuJour();

        // ── Test 6 : Authentification simulée ───────────────────────────
        testAuthentification("r.mansouri@sgcd.ma");

        printFooter();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 1 — Connexion simple
    // ════════════════════════════════════════════════════════════════════
    private static void testConnexion() {
        print("TEST 1", "Connexion à la base de données");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            ok("Connexion établie avec succès !");
            ok("Auto-commit     : " + conn.getAutoCommit());
            ok("Catalog         : " + conn.getCatalog());
            ok("Read-only       : " + conn.isReadOnly());
        } catch (SQLException e) {
            fail("Connexion impossible : " + e.getMessage());
            fail("SQLState : " + e.getSQLState() + " — ErrorCode : " + e.getErrorCode());
            System.out.println();
            System.out.println("  💡 SOLUTIONS POSSIBLES :");
            System.out.println("     • Vérifier que MySQL est démarré (service mysql start)");
            System.out.println("     • Vérifier DB_USER et DB_PASSWORD dans ce fichier");
            System.out.println("     • Vérifier que la base 'sgcd' existe (exécuter sgcd.sql)");
            System.out.println("     • Vérifier que le port 3306 est ouvert");
            System.exit(1);
        }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 2 — Version MySQL
    // ════════════════════════════════════════════════════════════════════
    private static void testVersionMySQL() {
        print("TEST 2", "Informations serveur MySQL");
        String sql = "SELECT VERSION() AS version, NOW() AS heure_serveur, @@character_set_database AS charset";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                ok("Version MySQL   : " + rs.getString("version"));
                ok("Heure serveur   : " + rs.getString("heure_serveur"));
                ok("Charset BD      : " + rs.getString("charset"));
            }
        } catch (SQLException e) { fail(e.getMessage()); }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 3 — Existence et nombre de lignes par table
    // ════════════════════════════════════════════════════════════════════
    private static void testTables() {
        print("TEST 3", "Vérification des tables");

        String[] tables = {
            "utilisateur", "administrateur", "dentiste", "assistante",
            "dentiste_jours_disponibles", "patient", "responsable_legal",
            "dossier_medical", "acte", "rendez_vous", "consultation",
            "consultation_acte", "document", "prescription",
            "medicament", "facture", "paiement"
        };

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int ok = 0, missing = 0;
            for (String table : tables) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + table);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        ok("  ✓ " + padRight(table, 30) + count + " ligne(s)");
                        ok++;
                    }
                } catch (SQLException e) {
                    fail("  ✗ " + padRight(table, 30) + "TABLE MANQUANTE → Exécuter sgcd.sql !");
                    missing++;
                }
            }
            System.out.println();
            ok("Résultat : " + ok + "/" + tables.length + " tables présentes"
                    + (missing > 0 ? " — " + missing + " manquante(s)" : " ✅"));
        } catch (SQLException e) { fail(e.getMessage()); }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 4 — Données de test
    // ════════════════════════════════════════════════════════════════════
    private static void testDonnees() {
        print("TEST 4", "Données de test");
        String sql = "SELECT role, COUNT(*) AS nb, GROUP_CONCAT(prenom, ' ', nom SEPARATOR ', ') AS noms"
                   + " FROM utilisateur GROUP BY role ORDER BY role";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ok(String.format("  %-15s → %d compte(s) : %s",
                        rs.getString("role"),
                        rs.getInt("nb"),
                        rs.getString("noms")));
            }
        } catch (SQLException e) { fail(e.getMessage()); }

        // Patients
        String sqlP = "SELECT COUNT(*) AS total, SUM(CASE WHEN TIMESTAMPDIFF(YEAR,dateNaissance,CURDATE())<18 THEN 1 ELSE 0 END) AS mineurs FROM patient";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlP);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                ok("  PATIENTS          → " + rs.getInt("total") + " total, " + rs.getInt("mineurs") + " mineur(s)");
        } catch (SQLException e) { fail(e.getMessage()); }

        // Actes
        String sqlA = "SELECT COUNT(*) AS nb, MIN(tarifBase) AS min, MAX(tarifBase) AS max, AVG(tarifBase) AS moy FROM acte";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sqlA);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                ok(String.format("  ACTES             → %d actes, tarifs : %.0f–%.0f MAD (moy : %.0f MAD)",
                        rs.getInt("nb"), rs.getDouble("min"),
                        rs.getDouble("max"), rs.getDouble("moy")));
        } catch (SQLException e) { fail(e.getMessage()); }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 5 — Requête métier : agenda du jour
    // ════════════════════════════════════════════════════════════════════
    private static void testAgendaDuJour() {
        print("TEST 5", "Agenda du jour (requête JOIN)");
        String sql =
            "SELECT rv.idRDV, TIME(rv.dateHeure) AS heure, rv.motif, rv.statut, rv.duree," +
            "       CONCAT(p.prenom,' ',p.nom)  AS patient," +
            "       CONCAT('Dr. ',u.prenom,' ',u.nom) AS dentiste " +
            "FROM rendez_vous rv " +
            "JOIN patient p     ON rv.idPatient  = p.idPatient " +
            "JOIN utilisateur u ON rv.idDentiste = u.idUtilisateur " +
            "WHERE DATE(rv.dateHeure) = CURDATE() " +
            "ORDER BY rv.dateHeure";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 0;
            System.out.printf("  %-6s %-10s %-12s %-20s %-22s %-20s%n",
                    "ID", "Heure", "Motif", "Statut", "Patient", "Dentiste");
            System.out.println("  " + "─".repeat(90));
            while (rs.next()) {
                System.out.printf("  %-6d %-10s %-12s %-20s %-22s %-20s%n",
                        rs.getLong("idRDV"),
                        rs.getString("heure"),
                        rs.getString("motif"),
                        rs.getString("statut"),
                        rs.getString("patient"),
                        rs.getString("dentiste"));
                count++;
            }
            if (count == 0) {
                warn("Aucun RDV aujourd'hui (les RDV sont horodatés à la date courante via NOW())");
            } else {
                ok(count + " rendez-vous trouvé(s) aujourd'hui");
            }
        } catch (SQLException e) { fail(e.getMessage()); }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // TEST 6 — Authentification simulée (login sans vérification mdp)
    // ════════════════════════════════════════════════════════════════════
    private static void testAuthentification(String login) {
        print("TEST 6", "Authentification — lecture du compte : " + login);
        String sql = "SELECT idUtilisateur, nom, prenom, role, statut, LEFT(motDePasse,20) AS hashDebut "
                   + "FROM utilisateur WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ok("ID          : " + rs.getLong("idUtilisateur"));
                    ok("Nom complet : " + rs.getString("prenom") + " " + rs.getString("nom"));
                    ok("Rôle        : " + rs.getString("role"));
                    ok("Statut      : " + rs.getString("statut"));
                    ok("Hash BCrypt : " + rs.getString("hashDebut") + "…");
                    ok("Mot de passe de test : sgcd1234  (vérification BCrypt en runtime via jbcrypt)");
                } else {
                    fail("Compte introuvable : " + login);
                }
            }
        } catch (SQLException e) { fail(e.getMessage()); }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    // Helpers d'affichage
    // ════════════════════════════════════════════════════════════════════
    private static void printBanner() {
        String now = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║  SGCD — Test de connexion MySQL                              ║");
        System.out.println("  ║  FST Fès — Licence Génie Informatique — 2025-2026            ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
        System.out.println("  Exécuté le : " + now);
        System.out.println();
    }

    private static void printFooter() {
        System.out.println("  ════════════════════════════════════════════════════════════════");
        System.out.println("  ✅  Tous les tests se sont terminés.");
        System.out.println("  ➜  L'application SGCD peut maintenant être déployée sur Tomcat.");
        System.out.println("  ════════════════════════════════════════════════════════════════");
        System.out.println();
    }

    private static void print(String tag, String msg) {
        System.out.println("  ┌─ " + tag + " : " + msg);
    }

    private static void ok(String msg)   { System.out.println("  │  ✓ " + msg); }
    private static void fail(String msg) { System.out.println("  │  ✗ [ERREUR] " + msg); }
    private static void warn(String msg) { System.out.println("  │  ⚠ " + msg); }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
