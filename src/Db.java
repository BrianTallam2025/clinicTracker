import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/clinic_tracker?useSSL=false&serverTimezone=UTC";

    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("CLINIC_DB_URL");
        String user = System.getenv("CLINIC_DB_USER");
        String password = System.getenv("CLINIC_DB_PASSWORD");

        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_URL;
        }
        if (user == null || user.trim().isEmpty()) {
            user = "root";
        }
        if (password == null) {
            password = "";
        }

        return DriverManager.getConnection(url, user, password);
    }
}