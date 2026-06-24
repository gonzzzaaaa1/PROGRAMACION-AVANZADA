package dao;

import db.Conexion;
import modelo.ObraSocial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObraSocialDAO implements DAO<ObraSocial> {

    @Override
    public boolean insertar(ObraSocial o) {
        String sql = "INSERT INTO obra_social (nombre, activa) VALUES (?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNombre());
            ps.setBoolean(2, o.isActiva());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar obra social: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(ObraSocial o) {
        String sql = "UPDATE obra_social SET nombre = ?, activa = ? WHERE id_obra_social = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNombre());
            ps.setBoolean(2, o.isActiva());
            ps.setInt(3, o.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar obra social: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM obra_social WHERE id_obra_social = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar obra social: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ObraSocial buscarPorId(int id) {
        String sql = "SELECT id_obra_social, nombre, activa FROM obra_social WHERE id_obra_social = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ObraSocial(rs.getInt("id_obra_social"),
                            rs.getString("nombre"), rs.getBoolean("activa"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar obra social: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ObraSocial> listarTodos() {
        return listar(false);
    }

    public List<ObraSocial> listarActivas() {
        return listar(true);
    }

    private List<ObraSocial> listar(boolean soloActivas) {
        List<ObraSocial> lista = new ArrayList<>();
        String sql = "SELECT id_obra_social, nombre, activa FROM obra_social " +
                (soloActivas ? "WHERE activa = TRUE " : "") + "ORDER BY nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ObraSocial(rs.getInt("id_obra_social"),
                        rs.getString("nombre"), rs.getBoolean("activa")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar obras sociales: " + e.getMessage());
        }
        return lista;
    }
}
