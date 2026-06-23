package dao;

import db.Conexion;
import modelo.Resultado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAO implements DAO<Resultado> {

    /**
     * Inserta un resultado. Si el resultado viene como autorizado, guarda tambien
     * la fecha de autorizacion y el medico que lo autoriza.
     */
    @Override
    public boolean insertar(Resultado r) {
        String sql;
        boolean autorizar = r.isAutorizado();
        if (autorizar) {
            sql = "INSERT INTO resultado (id_turno, descripcion, autorizado, fecha_autorizacion, id_medico_autoriza) " +
                    "VALUES (?, ?, TRUE, CURRENT_DATE, ?)";
        } else {
            sql = "INSERT INTO resultado (id_turno, descripcion, autorizado) VALUES (?, ?, FALSE)";
        }
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdTurno());
            ps.setString(2, r.getDescripcion());
            if (autorizar) {
                ps.setInt(3, r.getIdMedicoAutoriza());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar resultado: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Resultado r) {
        String sql = "UPDATE resultado SET descripcion = ? WHERE id_resultado = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getDescripcion());
            ps.setInt(2, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar resultado: " + e.getMessage());
        }
        return false;
    }

    /** Autoriza un resultado pendiente para que el paciente pueda verlo. */
    public boolean autorizar(int idResultado, int idMedico) {
        String sql = "UPDATE resultado SET autorizado = TRUE, fecha_autorizacion = CURRENT_DATE, " +
                "id_medico_autoriza = ? WHERE id_resultado = ? AND autorizado = FALSE";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idResultado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al autorizar resultado: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idResultado) {
        String sql = "DELETE FROM resultado WHERE id_resultado = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idResultado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar resultado: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Resultado buscarPorId(int idResultado) {
        String sql = "SELECT id_resultado, id_turno, descripcion, autorizado, " +
                "fecha_autorizacion, id_medico_autoriza FROM resultado WHERE id_resultado = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idResultado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resultado r = new Resultado();
                    r.setId(rs.getInt("id_resultado"));
                    r.setIdTurno(rs.getInt("id_turno"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setAutorizado(rs.getBoolean("autorizado"));
                    Date fa = rs.getDate("fecha_autorizacion");
                    if (fa != null) r.setFechaAutorizacion(fa.toLocalDate());
                    r.setIdMedicoAutoriza(rs.getInt("id_medico_autoriza"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar resultado: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Resultado> listarTodos() {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT id_resultado, id_turno, descripcion, autorizado, " +
                "fecha_autorizacion, id_medico_autoriza FROM resultado ORDER BY id_resultado DESC";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Resultado r = new Resultado();
                r.setId(rs.getInt("id_resultado"));
                r.setIdTurno(rs.getInt("id_turno"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setAutorizado(rs.getBoolean("autorizado"));
                Date fa = rs.getDate("fecha_autorizacion");
                if (fa != null) r.setFechaAutorizacion(fa.toLocalDate());
                r.setIdMedicoAutoriza(rs.getInt("id_medico_autoriza"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar resultados: " + e.getMessage());
        }
        return lista;
    }

    /** Resultados pendientes de autorizar de los turnos de un medico. */
    public List<Resultado> listarPendientesMedico(int idMedico) {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT r.id_resultado, r.descripcion, t.fecha, u.nombre, u.apellido, " +
                "te.nombre AS estudio " +
                "FROM resultado r " +
                "JOIN turno t ON r.id_turno = t.id_turno " +
                "JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "WHERE t.id_medico = ? AND r.autorizado = FALSE ORDER BY t.fecha";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resultado r = new Resultado();
                    r.setId(rs.getInt("id_resultado"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setNombrePaciente(rs.getString("nombre") + " " + rs.getString("apellido"));
                    r.setNombreEstudio(rs.getString("estudio"));
                    r.setFechaTurno(rs.getString("fecha"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar resultados pendientes: " + e.getMessage());
        }
        return lista;
    }

    /** Resultados autorizados de un paciente (lo que el paciente puede ver). */
    public List<Resultado> listarAutorizadosPaciente(int idPaciente) {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT r.id_resultado, r.descripcion, r.fecha_autorizacion, t.fecha, " +
                "u.nombre, u.apellido, te.nombre AS estudio " +
                "FROM resultado r " +
                "JOIN turno t ON r.id_turno = t.id_turno " +
                "JOIN usuario u ON t.id_medico = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "WHERE t.id_paciente = ? AND r.autorizado = TRUE " +
                "ORDER BY r.fecha_autorizacion DESC";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resultado r = new Resultado();
                    r.setId(rs.getInt("id_resultado"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setNombreMedico(rs.getString("nombre") + " " + rs.getString("apellido"));
                    r.setNombreEstudio(rs.getString("estudio"));
                    r.setFechaTurno(rs.getString("fecha"));
                    Date fa = rs.getDate("fecha_autorizacion");
                    if (fa != null) r.setFechaAutorizacion(fa.toLocalDate());
                    r.setAutorizado(true);
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar resultados del paciente: " + e.getMessage());
        }
        return lista;
    }
}
