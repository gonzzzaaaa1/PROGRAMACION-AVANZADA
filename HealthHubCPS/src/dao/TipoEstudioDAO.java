package dao;

import db.Conexion;
import modelo.Complejidad;
import modelo.TipoEstudio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEstudioDAO implements DAO<TipoEstudio> {

    @Override
    public boolean insertar(TipoEstudio t) {
        String sql = "INSERT INTO tipo_estudio (nombre, complejidad, tiempo_minutos, tarifa_base) " +
                "VALUES (?, ?::complejidad, ?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getComplejidad().name());
            ps.setInt(3, t.getTiempoMinutos());
            ps.setDouble(4, t.getTarifaBase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar tipo de estudio: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(TipoEstudio t) {
        String sql = "UPDATE tipo_estudio SET nombre = ?, complejidad = ?::complejidad, " +
                "tiempo_minutos = ?, tarifa_base = ? WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getComplejidad().name());
            ps.setInt(3, t.getTiempoMinutos());
            ps.setDouble(4, t.getTarifaBase());
            ps.setInt(5, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tipo de estudio: " + e.getMessage());
        }
        return false;
    }

    /** Cambia solo la tarifa base de un estudio. */
    public boolean actualizarTarifa(int id, double tarifa) {
        String sql = "UPDATE tipo_estudio SET tarifa_base = ? WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, tarifa);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tarifa: " + e.getMessage());
        }
        return false;
    }

    /** Cambia solo la duracion en minutos de un estudio. */
    public boolean actualizarTiempo(int id, int minutos) {
        String sql = "UPDATE tipo_estudio SET tiempo_minutos = ? WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minutos);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar tiempo: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM tipo_estudio WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar tipo de estudio: " + e.getMessage());
        }
        return false;
    }

    @Override
    public TipoEstudio buscarPorId(int id) {
        String sql = "SELECT id_tipo_estudio, nombre, complejidad, tiempo_minutos, tarifa_base " +
                "FROM tipo_estudio WHERE id_tipo_estudio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar tipo de estudio: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<TipoEstudio> listarTodos() {
        List<TipoEstudio> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_estudio, nombre, complejidad, tiempo_minutos, tarifa_base " +
                "FROM tipo_estudio ORDER BY nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tipos de estudio: " + e.getMessage());
        }
        return lista;
    }

    private TipoEstudio mapear(ResultSet rs) throws SQLException {
        TipoEstudio t = new TipoEstudio();
        t.setId(rs.getInt("id_tipo_estudio"));
        t.setNombre(rs.getString("nombre"));
        t.setComplejidad(Complejidad.valueOf(rs.getString("complejidad")));
        t.setTiempoMinutos(rs.getInt("tiempo_minutos"));
        t.setTarifaBase(rs.getDouble("tarifa_base"));
        return t;
    }
}
