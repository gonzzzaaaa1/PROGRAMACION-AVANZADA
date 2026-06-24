package dao;

import db.Conexion;
import modelo.ArchivoAdjunto;
import modelo.FormatoArchivo;
import modelo.TipoArchivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoAdjuntoDAO implements DAO<ArchivoAdjunto> {

    @Override
    public boolean insertar(ArchivoAdjunto a) {
        String sql = "INSERT INTO archivo_adjunto (id_historia, id_medico_carga, tipo, formato, url, fecha_carga) " +
                "VALUES (?, ?, ?::tipo_archivo, ?::formato_archivo, ?, CURRENT_DATE)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getIdHistoria());
            ps.setInt(2, a.getIdMedicoCarga());
            ps.setString(3, a.getTipo().name());
            ps.setString(4, a.getFormato().name());
            ps.setString(5, a.getUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar archivo adjunto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(ArchivoAdjunto a) {
        String sql = "UPDATE archivo_adjunto SET tipo = ?::tipo_archivo, formato = ?::formato_archivo, " +
                "url = ? WHERE id_archivo = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getTipo().name());
            ps.setString(2, a.getFormato().name());
            ps.setString(3, a.getUrl());
            ps.setInt(4, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar archivo adjunto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idArchivo) {
        String sql = "DELETE FROM archivo_adjunto WHERE id_archivo = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArchivo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar archivo adjunto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArchivoAdjunto buscarPorId(int idArchivo) {
        String sql = "SELECT id_archivo, id_historia, id_medico_carga, tipo, formato, url, fecha_carga " +
                "FROM archivo_adjunto WHERE id_archivo = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idArchivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar archivo adjunto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ArchivoAdjunto> listarTodos() {
        return ejecutar("SELECT id_archivo, id_historia, id_medico_carga, tipo, formato, url, fecha_carga " +
                "FROM archivo_adjunto ORDER BY fecha_carga DESC", 0);
    }

    /** Archivos de una historia clinica. */
    public List<ArchivoAdjunto> listarPorHistoria(int idHistoria) {
        return ejecutar("SELECT id_archivo, id_historia, id_medico_carga, tipo, formato, url, fecha_carga " +
                "FROM archivo_adjunto WHERE id_historia = ? ORDER BY fecha_carga DESC", idHistoria);
    }

    /** Archivos de la historia clinica de un paciente. */
    public List<ArchivoAdjunto> listarPorPaciente(int idPaciente) {
        List<ArchivoAdjunto> lista = new ArrayList<>();
        String sql = "SELECT a.id_archivo, a.id_historia, a.id_medico_carga, a.tipo, a.formato, a.url, a.fecha_carga " +
                "FROM archivo_adjunto a JOIN historia_clinica h ON a.id_historia = h.id_historia " +
                "WHERE h.id_paciente = ? ORDER BY a.fecha_carga DESC";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar archivos del paciente: " + e.getMessage());
        }
        return lista;
    }

    private List<ArchivoAdjunto> ejecutar(String sql, int idParam) {
        List<ArchivoAdjunto> lista = new ArrayList<>();
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (idParam > 0) ps.setInt(1, idParam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar archivos adjuntos: " + e.getMessage());
        }
        return lista;
    }

    private ArchivoAdjunto mapear(ResultSet rs) throws SQLException {
        ArchivoAdjunto a = new ArchivoAdjunto();
        a.setId(rs.getInt("id_archivo"));
        a.setIdHistoria(rs.getInt("id_historia"));
        a.setIdMedicoCarga(rs.getInt("id_medico_carga"));
        a.setTipo(TipoArchivo.valueOf(rs.getString("tipo")));
        a.setFormato(FormatoArchivo.valueOf(rs.getString("formato")));
        a.setUrl(rs.getString("url"));
        Date f = rs.getDate("fecha_carga");
        if (f != null) a.setFechaCarga(f.toLocalDate());
        return a;
    }
}
