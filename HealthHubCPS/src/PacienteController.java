import java.sql.*;
import java.time.LocalDate;

public class PacienteController {


    /**
     * Verifica si un paciente ya tiene datos completos
     */
    public boolean pacienteTieneDatos(int idPaciente) {
        String sql = "SELECT id_usuario FROM paciente WHERE id_usuario = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar datos del paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Completa los datos del paciente (si aún no los tiene)
     */
    public boolean completarDatosPaciente(int idPaciente, LocalDate fechaNacimiento,
                                          String domicilio, int idObraSocial) {
        String sql = "INSERT INTO paciente (id_usuario, fecha_nacimiento, domicilio, id_obra_social) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id_usuario) DO UPDATE SET " +
                "fecha_nacimiento = EXCLUDED.fecha_nacimiento, " +
                "domicilio = EXCLUDED.domicilio, " +
                "id_obra_social = EXCLUDED.id_obra_social";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setDate(2, Date.valueOf(fechaNacimiento));
            ps.setString(3, domicilio);
            ps.setInt(4, idObraSocial);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al completar datos del paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un dato específico del usuario
     */
    public boolean actualizarUsuario(int idUsuario, String campo, String valor) {
        // Lista de campos permitidos para evitar SQL injection
        if (!campo.matches("nombre|apellido|email|telefono")) {
            System.out.println("Campo inválido: " + campo);
            return false;
        }

        String sql = "UPDATE usuario SET " + campo + " = ? WHERE id_usuario = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene los datos del paciente
     */
    public String[] obtenerDatosPaciente(int idPaciente) {
        String sql = "SELECT u.nombre, u.apellido, u.email, u.telefono, p.fecha_nacimiento, " +
                "p.domicilio, os.nombre as obra_social " +
                "FROM usuario u " +
                "LEFT JOIN paciente p ON u.id_usuario = p.id_usuario " +
                "LEFT JOIN obra_social os ON p.id_obra_social = os.id_obra_social " +
                "WHERE u.id_usuario = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("telefono"),
                            rs.getString("fecha_nacimiento"),
                            rs.getString("domicilio"),
                            rs.getString("obra_social")
                    };
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener datos del paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
