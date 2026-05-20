import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/healthhubcps";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";

    private static Connection conect;
    private static Conexion instance;

    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
            conect = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Se conectó a HealthHubCPS");
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró el driver de PostgreSQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("No se conectó: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    public Connection getConnection() {
        return conect;
    }
}