package dao;

import db.Conexion;
import modelo.Consultorio;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultorioDAO implements DAO<Consultorio> {

    @Override
    public boolean insertar(Consultorio c) {
        String sql = "INSERT INTO consultorio (numero, ubicacion) VALUES (?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setString(2, c.getUbicacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar consultorio: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Consultorio c) {
        String sql = "UPDATE consultorio SET numero = ?, ubicacion = ? WHERE id_consultorio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setString(2, c.getUbicacion());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar consultorio: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM consultorio WHERE id_consultorio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar consultorio: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Consultorio buscarPorId(int id) {
        String sql = "SELECT id_consultorio, numero, ubicacion FROM consultorio WHERE id_consultorio = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Consultorio(rs.getInt("id_consultorio"),
                            rs.getString("numero"), rs.getString("ubicacion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar consultorio: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Consultorio> listarTodos() {
        List<Consultorio> lista = new ArrayList<>();
        String sql = "SELECT id_consultorio, numero, ubicacion FROM consultorio ORDER BY numero";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Consultorio(rs.getInt("id_consultorio"),
                        rs.getString("numero"), rs.getString("ubicacion")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar consultorios: " + e.getMessage());
        }
        return lista;
    }


    public Consultorio buscarLibre(LocalDate fecha, LocalTime hora) {
        String sql = "SELECT c.id_consultorio, c.numero, c.ubicacion FROM consultorio c " +
                "WHERE c.id_consultorio NOT IN ( " +
                "   SELECT t.id_consultorio FROM turno t " +
                "   WHERE t.fecha = ? AND t.hora = ? AND t.estado = 'AGENDADO' " +
                ") ORDER BY c.numero LIMIT 1";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Consultorio(rs.getInt("id_consultorio"),
                            rs.getString("numero"), rs.getString("ubicacion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar consultorio libre: " + e.getMessage());
        }
        return null;
    }

    public List<Consultorio> consultoriosDeMedico(int idMedico, int dias) {
        List<Consultorio> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT c.id_consultorio, c.numero, c.ubicacion " +
                "FROM turno t JOIN consultorio c ON t.id_consultorio = c.id_consultorio " +
                "WHERE t.id_medico = ? AND t.estado = 'AGENDADO' " +
                "AND t.fecha BETWEEN CURRENT_DATE AND CURRENT_DATE + ? " +
                "ORDER BY c.numero";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, dias);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Consultorio(rs.getInt("id_consultorio"),
                            rs.getString("numero"), rs.getString("ubicacion")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar consultorios del medico: " + e.getMessage());
        }
        return lista;
    }
}
