import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDate;

public class UsuarioController {

    public Usuario validarLogin(String dni, String contrasenia) {
        Usuario user = null;
        String sql = "SELECT id_usuario, dni, nombre, apellido, email, telefono, rol, activo " +
                "FROM usuario " +
                "WHERE dni = ? AND contrasenia = ? AND activo = TRUE";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, contrasenia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario();
                    user.setId(rs.getInt("id_usuario"));
                    user.setDni(rs.getString("dni"));
                    user.setNombre(rs.getString("nombre"));
                    user.setApellido(rs.getString("apellido"));
                    user.setEmail(rs.getString("email"));
                    user.setTelefono(rs.getString("telefono"));
                    user.setRol(rs.getString("rol"));
                    user.setActivo(rs.getBoolean("activo"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar login: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Registra un nuevo usuario en la base y devuelve su ID generado.
     * @return el id_usuario generado, o -1 si falla
     */
    public int registrarUsuario(String dni, String contrasenia, String nombre,
                                String apellido, String email, String telefono,
                                String rol) {
        String sql = "INSERT INTO usuario (dni, contrasenia, nombre, apellido, email, telefono, rol, fecha_alta) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?::rol_usuario, ?) " +
                "RETURNING id_usuario";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, contrasenia);
            ps.setString(3, nombre);
            ps.setString(4, apellido);
            ps.setString(5, email);
            ps.setString(6, telefono);
            ps.setString(7, rol.toUpperCase());
            ps.setDate(8, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Registra los datos específicos del paciente, vinculado a un usuario ya creado.
     */
    public boolean registrarPaciente(int idUsuario, LocalDate fechaNacimiento,
                                     String domicilio, int idObraSocial) {
        String sql = "INSERT INTO paciente (id_usuario, fecha_nacimiento, domicilio, id_obra_social) " +
                "VALUES (?, ?, ?, ?)";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setDate(2, Date.valueOf(fechaNacimiento));
            ps.setString(3, domicilio);
            ps.setInt(4, idObraSocial);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registra los datos específicos del médico, vinculado a un usuario ya creado.
     */
    public boolean registrarMedico(int idUsuario, String matricula, int idEspecialidad) {
        String sql = "INSERT INTO medico (id_usuario, matricula, id_especialidad) " +
                "VALUES (?, ?, ?)";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, matricula);
            ps.setInt(3, idEspecialidad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar medico: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Crea la historia clínica vacía para un paciente recién registrado.
     */
    public boolean crearHistoriaClinica(int idPaciente) {
        String sql = "INSERT INTO historia_clinica (id_paciente, fecha_creacion) VALUES (?, ?)";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al crear historia clinica: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}