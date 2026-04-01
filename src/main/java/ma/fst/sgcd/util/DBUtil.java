package ma.fst.sgcd.util;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    private static DataSource dataSource;
    private DBUtil() {}

    public static void init(String url, String user, String password) {
        try {
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            ds.setURL(url); ds.setUser(user); ds.setPassword(password);
            dataSource = ds;
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'initialiser le DataSource MySQL", e);
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null)
            throw new IllegalStateException("DataSource non initialise.");
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
