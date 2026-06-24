package dao;

import db.Conexion;
import modelo.Cobertura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoberturaDAO {

    public List<Cobertura> listarTodas() {
        List<Cobertura> lista = new ArrayList<>();
        String sql = "SELECT c.id_obra_social, c.id_tipo_estudio, c.porcentaje_cobertura, c.vigente, " +
                "os.nombre AS obra_social, te.nombre AS estudio " +
                "FROM cobertura c " +
                "JOIN obra_social os ON c.id_obra_social = os.id_obra_social " +
                "JOIN tipo_estudio te ON c.id_tipo_estudio = te.id_tipo_estudio " +
                "ORDER BY os.nombre, te.nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cobertura c = new Cobertura();
                c.setIdObraSocial(rs.getInt("id_obra_social"));
                c.setIdTipoEstudio(rs.getInt("id_tipo_estudio"));
                c.setPorcentajeCobertura(rs.getDouble("porcentaje_cobertura"));
                c.setVigente(rs.getBoolean("vigente"));
                c.setNombreObraSocial(rs.getString("obra_social"));
                c.setNombreTipoEstudio(rs.getString("estudio"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coberturas: " + e.getMessage());
        }
        return lista;
    }

    public double obtenerPorcentajeVigente(int idObraSocial, int idTipoEstudio) {
        String sql = "SELECT porcentaje_cobertura FROM cobertura " +
                "WHERE id_obra_social = ? AND id_tipo_estudio = ? AND vigente = TRUE";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idObraSocial);
            ps.setInt(2, idTipoEstudio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("porcentaje_cobertura");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cobertura: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizarPorcentaje(int idObraSocial, int idTipoEstudio, double porcentaje) {
        String sql = "UPDATE cobertura SET porcentaje_cobertura = ? " +
                "WHERE id_obra_social = ? AND id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, porcentaje);
            ps.setInt(2, idObraSocial);
            ps.setInt(3, idTipoEstudio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cobertura: " + e.getMessage());
        }
        return false;
    }
}