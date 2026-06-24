package dao;

import db.Conexion;
import modelo.HistoriaClinica;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoriaClinicaDAO implements DAO<HistoriaClinica> {

    @Override
    public boolean insertar(HistoriaClinica h) {
        String sql = "INSERT INTO historia_clinica (id_paciente, fecha_creacion) VALUES (?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getIdPaciente());
            ps.setDate(2, Date.valueOf(h.getFechaCreacion() != null ? h.getFechaCreacion() : LocalDate.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear historia clinica: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(HistoriaClinica h) {
        String sql = "UPDATE historia_clinica SET observaciones = ? WHERE id_historia = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, h.getObservaciones());
            ps.setInt(2, h.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar historia clinica: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idHistoria) {
        String sql = "DELETE FROM historia_clinica WHERE id_historia = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHistoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar historia clinica: " + e.getMessage());
        }
        return false;
    }

    @Override
    public HistoriaClinica buscarPorId(int idHistoria) {
        return buscar("WHERE id_historia = ?", idHistoria);
    }

    /** Busca la historia clinica de un paciente. */
    public HistoriaClinica buscarPorPaciente(int idPaciente) {
        return buscar("WHERE id_paciente = ?", idPaciente);
    }

    private HistoriaClinica buscar(String filtro, int id) {
        String sql = "SELECT id_historia, id_paciente, fecha_creacion, observaciones " +
                "FROM historia_clinica " + filtro;
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar historia clinica: " + e.getMessage());
        }
        return null;
    }

    public int obtenerIdHistoria(int idPaciente) {
        HistoriaClinica h = buscarPorPaciente(idPaciente);
        return h != null ? h.getId() : -1;
    }

    public int obtenerIdHistoriaDeTurno(int idTurno) {
        String sql = "SELECT h.id_historia FROM historia_clinica h " +
                "JOIN turno t ON t.id_paciente = h.id_paciente WHERE t.id_turno = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTurno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_historia");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historia del turno: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<HistoriaClinica> listarTodos() {
        List<HistoriaClinica> lista = new ArrayList<>();
        String sql = "SELECT id_historia, id_paciente, fecha_creacion, observaciones FROM historia_clinica";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar historias clinicas: " + e.getMessage());
        }
        return lista;
    }

    private HistoriaClinica mapear(ResultSet rs) throws SQLException {
        HistoriaClinica h = new HistoriaClinica();
        h.setId(rs.getInt("id_historia"));
        h.setIdPaciente(rs.getInt("id_paciente"));
        Date f = rs.getDate("fecha_creacion");
        if (f != null) h.setFechaCreacion(f.toLocalDate());
        h.setObservaciones(rs.getString("observaciones"));
        return h;
    }
}
