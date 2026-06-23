package dao;

import db.Conexion;
import modelo.Especialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO implements DAO<Especialidad> {

    @Override
    public boolean insertar(Especialidad e) {
        String sql = "INSERT INTO especialidad (nombre) VALUES (?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al insertar especialidad: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Especialidad e) {
        String sql = "UPDATE especialidad SET nombre = ? WHERE id_especialidad = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setInt(2, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al actualizar especialidad: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM especialidad WHERE id_especialidad = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al eliminar especialidad: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public Especialidad buscarPorId(int id) {
        String sql = "SELECT id_especialidad, nombre FROM especialidad WHERE id_especialidad = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Especialidad(rs.getInt("id_especialidad"), rs.getString("nombre"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar especialidad: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Especialidad> listarTodos() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT id_especialidad, nombre FROM especialidad ORDER BY nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Especialidad(rs.getInt("id_especialidad"), rs.getString("nombre")));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar especialidades: " + ex.getMessage());
        }
        return lista;
    }
}
