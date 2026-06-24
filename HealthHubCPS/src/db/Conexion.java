package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion {

    private static Conexion instancia;
    private Connection conexion;

    private Conexion() {
        try {
            Properties prop = cargarConfiguracion();
            String url = prop.getProperty("db.url", "jdbc:postgresql://localhost:5432/healthhubcps");
            String user = prop.getProperty("db.user", "postgres");
            String password = prop.getProperty("db.password", "postgres");

            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexion establecida con HealthHubCPS");
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontro el driver de PostgreSQL. " +
                    "Verifica que el .jar este agregado al proyecto.");
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a la base de datos: " + e.getMessage());
            System.out.println("Revisa usuario/contrasena en config.properties y que " +
                    "la base 'healthhubcps' exista.");
        }
    }

    private Properties cargarConfiguracion() {
        Properties prop = new Properties();
        try (InputStream in = Conexion.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                prop.load(in);
            } else {
                System.out.println("No se encontro config.properties; se usaran valores por defecto.");
            }
        } catch (Exception e) {
            System.out.println("Error al leer config.properties: " + e.getMessage());
        }
        return prop;
    }

    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                instancia = new Conexion();
                return instancia.conexion;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la conexion: " + e.getMessage());
        }
        return conexion;
    }
}