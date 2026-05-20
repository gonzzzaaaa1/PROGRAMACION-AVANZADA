import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioController {

    public Usuario validarLogin(String dni, String contrasenia) {
        Usuario user = null;
        String sql = "SELECT id_usuario, dni, nombre, apellido, email, telefono, rol, activo " +
                "FROM usuario " +
                "WHERE dni = ? AND contrasenia = ? AND activo = TRUE";

        try (Connection con = Conexion.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
}