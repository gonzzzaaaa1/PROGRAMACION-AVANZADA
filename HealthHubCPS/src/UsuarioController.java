import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioController {

    public String validarLogin(String dni, String password) {
        Usuario user = null;
        String sql = "SELECT rol FROM usuarios WHERE dni = ? AND password = ?";

        try (Connection con = Conexion.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setDni(rs.getString("dni"));
                user.setNombre(rs.getString("nombre"));
                user.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.out.println("Error al validar: " + e.getMessage());
        }
        return user;
    }
}
