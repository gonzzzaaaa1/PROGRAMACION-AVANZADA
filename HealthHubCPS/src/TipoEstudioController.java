import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEstudioController {

    /**
     * Devuelve los tipos de estudio en formato "id - nombre ($tarifa) [complejidad]"
     */
    public List<String> listarTiposEstudio() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_estudio, nombre, complejidad, tarifa_base " +
                "FROM tipo_estudio " +
                "ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_tipo_estudio");
                String nombre = rs.getString("nombre");
                String complejidad = rs.getString("complejidad");
                double tarifa = rs.getDouble("tarifa_base");
                lista.add(id + " - " + nombre + " ($" + tarifa + ") [" + complejidad + "]");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tipos de estudio: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Actualiza la tarifa base de un tipo de estudio.
     */
    public boolean actualizarTarifa(int idTipoEstudio, double nuevaTarifa) {
        String sql = "UPDATE tipo_estudio SET tarifa_base = ? WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, nuevaTarifa);
            ps.setInt(2, idTipoEstudio);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tarifa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza el tiempo en minutos de un tipo de estudio.
     */
    public boolean actualizarTiempo(int idTipoEstudio, int nuevosMinutos) {
        String sql = "UPDATE tipo_estudio SET tiempo_minutos = ? WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevosMinutos);
            ps.setInt(2, idTipoEstudio);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tiempo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Devuelve los tipos de estudio en formato  "id - nombre (tiempo min)"
     */
    public List<String> listarTiposEstudioConTiempo() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_estudio, nombre, tiempo_minutos " +
                "FROM tipo_estudio ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_tipo_estudio");
                String nombre = rs.getString("nombre");
                int tiempo = rs.getInt("tiempo_minutos");
                lista.add(id + " - " + nombre + " (" + tiempo + " min)");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tipos de estudio: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}

