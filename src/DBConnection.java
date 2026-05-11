import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL ="jdbc:sqlserver://localhost\\SQLEXPRESS;" +
            "databaseName=The Multi-Specialty Healthcare Network;" +
            "integratedSecurity=true;" +
            "trustServerCertificate=true;"
           ;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}