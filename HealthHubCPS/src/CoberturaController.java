import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoberturaController {

    /**
     * Devuelve todas las coberturas con los nombres legibles de obra social y estudio.
     * Formato: "idOS-idTE - ObraSocial / Estudio: XX.XX%"
     */
    public List<String> listarCoberturas() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT c.id_obra_social, c.id_tipo_estudio, c.porcentaje_cobertura, " +
                "       o.nombre AS obra, t.nombre AS estudio " +
                "FROM cobertura c " +
                "JOIN obra_social o ON c.id_obra_social = o.id_obra_social " +
                "JOIN tipo_estudio t ON c.id_tipo_estudio = t.id_tipo_estudio " +
                "WHERE c.vigente = TRUE " +
                "ORDER BY o.nombre, t.nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idOS = rs.getInt("id_obra_social");
                int idTE = rs.getInt("id_tipo_estudio");
                double porcentaje = rs.getDouble("porcentaje_cobertura");
                String obra = rs.getString("obra");
                String estudio = rs.getString("estudio");
                lista.add(idOS + "-" + idTE + " - " + obra + " / " + estudio + ": " + porcentaje + "%");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coberturas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Actualiza el porcentaje de cobertura de una combinación obra social - tipo estudio.
     */
    public boolean actualizarCobertura(int idObraSocial, int idTipoEstudio, double nuevoPorcentaje) {
        String sql = "UPDATE cobertura SET porcentaje_cobertura = ? " +
                "WHERE id_obra_social = ? AND id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, nuevoPorcentaje);
            ps.setInt(2, idObraSocial);
            ps.setInt(3, idTipoEstudio);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cobertura: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
