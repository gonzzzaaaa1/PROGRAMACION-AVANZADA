import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultorioController {

    /**
     * Devuelve los consultorios en formato "id - numero (ubicacion)"
     */
    public List<String> listarConsultorios() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_consultorio, numero, ubicacion FROM consultorio ORDER BY numero";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_consultorio");
                String numero = rs.getString("numero");
                String ubicacion = rs.getString("ubicacion");
                lista.add(id + " - " + numero + " (" + ubicacion + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar consultorios: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Devuelve los consultorios en los que un medico tiene turnos en los proximos N dias,
     * con la cantidad de turnos por consultorio.
     */
    public List<String> consultoriosDeMedico(int idMedico, int diasFuturos) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT c.numero, c.ubicacion, COUNT(t.id_turno) AS cant_turnos " +
                "FROM turno t " +
                "JOIN consultorio c ON t.id_consultorio = c.id_consultorio " +
                "WHERE t.id_medico = ? " +
                "  AND t.estado = 'AGENDADO' " +
                "  AND t.fecha BETWEEN CURRENT_DATE AND CURRENT_DATE + ? " +
                "GROUP BY c.id_consultorio, c.numero, c.ubicacion " +
                "ORDER BY cant_turnos DESC";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, diasFuturos);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String numero = rs.getString("numero");
                    String ubicacion = rs.getString("ubicacion");
                    int cant = rs.getInt("cant_turnos");
                    String texto = "Consultorio " + numero + " (" + ubicacion + "): " + cant + " turno";
                    if (cant != 1) texto += "s";
                    lista.add(texto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar consultorios del medico: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
