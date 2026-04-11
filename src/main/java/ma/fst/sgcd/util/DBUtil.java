package ma.fst.sgcd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitaire de connexion JDBC — DriverManager (méthode standard de cours).
 *
 * ⚠️  MODIFIER les 3 constantes ci-dessous selon votre installation MySQL.
 */
public class DBUtil {

    // ──────────────────────────────────────────────────────────────────────
    //   MODIFIER ICI : vos paramètres MySQL
    // ──────────────────────────────────────────────────────────────────────
    private static final String URL =
            "jdbc:mysql://localhost:3306/sgcd"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Africa/Casablanca"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true";

    private static final String USER     = "root";  // ← votre user MySQL
    private static final String PASSWORD = "root";       // ← votre mot de passe MySQL
    // ──────────────────────────────────────────────────────────────────────

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable.", e);
        }
    }

    private DBUtil() {}

    /**
     * Retourne une connexion MySQL.
     * À utiliser dans un try-with-resources :
     *   try (Connection conn = DBUtil.getConnection(); ...) { ... }
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
