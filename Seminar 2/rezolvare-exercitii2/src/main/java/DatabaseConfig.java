import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL =  "jdbc:oracle:thin:@//193.226.34.57:1521/orclpdb.docker.internal";
    private static final String USERNAME = "<REPLACE_USERNAME>";
    private static final String PASSWORD = "<REPLACE_PASSWORD>";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}