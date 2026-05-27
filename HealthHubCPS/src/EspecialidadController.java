import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadController {

    public List<String> listarEspecialidades() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_especialidad, nombre FROM especialidad ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_especialidad");
                String nombre = rs.getString("nombre");
                lista.add(id + " - " + nombre);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}