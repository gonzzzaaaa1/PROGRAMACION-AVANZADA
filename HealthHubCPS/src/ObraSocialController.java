import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObraSocialController {

    /**
     * Devuelve una lista de obras sociales activas en formato "id - nombre"
     * para mostrar al admin al elegir.
     */
    public List<String> listarObrasSociales() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_obra_social, nombre FROM obra_social WHERE activa = TRUE ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_obra_social");
                String nombre = rs.getString("nombre");
                lista.add(id + " - " + nombre);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar obras sociales: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
