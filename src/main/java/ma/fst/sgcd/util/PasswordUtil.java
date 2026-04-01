package ma.fst.sgcd.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private PasswordUtil() {}
    public static String hash(String p)             { return BCrypt.hashpw(p, BCrypt.gensalt(12)); }
    public static boolean check(String p, String h) {
        try { return BCrypt.checkpw(p, h); } catch (Exception e) { return false; }
    }
}
