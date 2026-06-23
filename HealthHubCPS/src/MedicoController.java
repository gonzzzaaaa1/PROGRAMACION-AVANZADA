import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicoController {

    public Medico buscarMedicoPorId(int idUsuario) {
        Medico medico = null;
        String sql = "SELECT m.id_usuario, m.matricula, m.id_especialidad, " +
                "u.nombre, u.apellido, e.nombre AS especialidad " +
                "FROM medico m " +
                "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                "JOIN especialidad e ON m.id_especialidad = e.id_especialidad " +
                "WHERE m.id_usuario = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    medico = new Medico();
                    medico.setIdUsuario(rs.getInt("id_usuario"));
                    medico.setMatricula(rs.getString("matricula"));
                    medico.setIdEspecialidad(rs.getInt("id_especialidad"));
                    medico.setNombre(rs.getString("nombre"));
                    medico.setApellido(rs.getString("apellido"));
                    medico.setEspecialidadNombre(rs.getString("especialidad"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar medico: " + e.getMessage());
            e.printStackTrace();
        }
        return medico;
    }

    public List<Medico> listarMedicos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT m.id_usuario, m.matricula, m.id_especialidad, " +
                "u.nombre, u.apellido, e.nombre AS especialidad " +
                "FROM medico m " +
                "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                "JOIN especialidad e ON m.id_especialidad = e.id_especialidad " +
                "WHERE u.activo = TRUE";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medico medico = new Medico();
                medico.setIdUsuario(rs.getInt("id_usuario"));
                medico.setMatricula(rs.getString("matricula"));
                medico.setIdEspecialidad(rs.getInt("id_especialidad"));
                medico.setNombre(rs.getString("nombre"));
                medico.setApellido(rs.getString("apellido"));
                medico.setEspecialidadNombre(rs.getString("especialidad"));
                lista.add(medico);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar medicos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean registrarMedico(int idUsuario, String matricula, int idEspecialidad) {
        String error;

        error = MedicoValidacion.validarIdUsuario(idUsuario);
        if (error != null) {
            System.out.println("Error de validacion: " + error);
            return false;
        }
        error = MedicoValidacion.validarMatricula(matricula);
        if (error != null) {
            System.out.println("Error de validacion: " + error);
            return false;
        }
        error = MedicoValidacion.validarIdEspecialidad(idEspecialidad);
        if (error != null) {
            System.out.println("Error de validacion: " + error);
            return false;
        }

        String sql = "INSERT INTO medico (id_usuario, matricula, id_especialidad) VALUES (?, ?, ?)";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, matricula.trim());
            ps.setInt(3, idEspecialidad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar medico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarMatricula(int idUsuario, String matricula) {
        String error = MedicoValidacion.validarMatricula(matricula);
        if (error != null) {
            System.out.println("Error de validacion: " + error);
            return false;
        }

        String sql = "UPDATE medico SET matricula = ? WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula.trim());
            ps.setInt(2, idUsuario);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar matricula: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean darDeBajaMedico(int idUsuario) {
        String sql = "UPDATE usuario SET activo = FALSE WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al dar de baja medico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
