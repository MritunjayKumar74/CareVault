import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER     = "postgres.anvbyrixoyjxcjyaqdak";
    private static final String PASSWORD = "DctorWhoPeter12@#";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found. Add postgresql.jar to classpath.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✓ Supabase connection successful!");
            }
        } catch (SQLException e) {
            System.out.println("✗ Connection failed: " + e.getMessage());
        }
    }
}
