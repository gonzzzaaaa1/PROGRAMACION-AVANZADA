package dao;

import db.Conexion;
import modelo.EstadoTurno;
import modelo.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO implements DAO<Turno> {

    @Override
    public boolean insertar(Turno t) {
        String sql = "INSERT INTO turno (fecha, hora, estado, monto_final, id_paciente, " +
                "id_medico, id_consultorio, id_tipo_estudio) " +
                "VALUES (?, ?, ?::estado_turno, ?, ?, ?, ?, ?) RETURNING id_turno";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(t.getFecha()));
            ps.setTime(2, Time.valueOf(t.getHora()));
            ps.setString(3, t.getEstado().name());
            ps.setDouble(4, t.getMontoFinal());
            ps.setInt(5, t.getIdPaciente());
            ps.setInt(6, t.getIdMedico());
            ps.setInt(7, t.getIdConsultorio());
            ps.setInt(8, t.getIdTipoEstudio());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t.setId(rs.getInt("id_turno"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar turno: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Turno t) {
        String sql = "UPDATE turno SET fecha = ?, hora = ?, estado = ?::estado_turno, " +
                "monto_final = ?, motivo_cancel = ? WHERE id_turno = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(t.getFecha()));
            ps.setTime(2, Time.valueOf(t.getHora()));
            ps.setString(3, t.getEstado().name());
            ps.setDouble(4, t.getMontoFinal());
            ps.setString(5, t.getMotivoCancel());
            ps.setInt(6, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar turno: " + e.getMessage());
        }
        return false;
    }

    public boolean cancelar(int idTurno, String motivo) {
        String sql = "UPDATE turno SET estado = 'CANCELADO'::estado_turno, motivo_cancel = ? " +
                "WHERE id_turno = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idTurno);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cancelar turno: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idTurno) {
        String sql = "DELETE FROM turno WHERE id_turno = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTurno);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar turno: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Turno buscarPorId(int idTurno) {
        String sql = baseSelect() + "WHERE t.id_turno = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTurno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar turno: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Turno> listarTodos() {
        return ejecutarLista(baseSelect() + "ORDER BY t.fecha DESC, t.hora DESC", 0);
    }

    public List<Turno> listarPorPaciente(int idPaciente) {
        return ejecutarLista(baseSelect() + "WHERE t.id_paciente = ? " +
                "ORDER BY t.fecha DESC, t.hora DESC", idPaciente);
    }

    public List<Turno> listarActivosPaciente(int idPaciente) {
        return ejecutarLista(baseSelect() + "WHERE t.id_paciente = ? AND t.estado = 'AGENDADO' " +
                "ORDER BY t.fecha, t.hora", idPaciente);
    }

    public List<LocalTime> listarHorasOcupadasMedico(int idMedico, LocalDate fecha) {
        List<LocalTime> horas = new ArrayList<>();
        String sql = "SELECT hora FROM turno " +
                "WHERE id_medico = ? AND fecha = ? AND estado = 'AGENDADO' " +
                "ORDER BY hora";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Time h = rs.getTime("hora");
                    if (h != null) horas.add(h.toLocalTime());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar horas ocupadas: " + e.getMessage());
        }
        return horas;
    }

    public int contarConsultoriosOcupados(LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(DISTINCT id_consultorio) AS ocupados FROM turno " +
                "WHERE fecha = ? AND hora = ? AND estado = 'AGENDADO'";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("ocupados");
            }
        } catch (SQLException e) {
            System.out.println("Error al contar consultorios ocupados: " + e.getMessage());
        }
        return 0;
    }


    public boolean medicoOcupado(int idMedico, LocalDate fecha, LocalTime hora) {
        String sql = "SELECT 1 FROM turno " +
                "WHERE id_medico = ? AND fecha = ? AND hora = ? AND estado = 'AGENDADO' LIMIT 1";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar disponibilidad del medico: " + e.getMessage());
        }
        return false;
    }

    public List<Turno> listarAgendaMedico(int idMedico) {
        return ejecutarLista(baseSelect() + "WHERE t.id_medico = ? AND t.estado = 'AGENDADO' " +
                "ORDER BY t.fecha, t.hora", idMedico);
    }

    public List<Turno> listarSinResultadoMedico(int idMedico) {
        return ejecutarLista(baseSelect() + "WHERE t.id_medico = ? AND t.estado <> 'CANCELADO' " +
                "AND t.id_turno NOT IN (SELECT id_turno FROM resultado) " +
                "ORDER BY t.fecha DESC, t.hora DESC", idMedico);
    }

    private String baseSelect() {
        return "SELECT t.id_turno, t.fecha, t.hora, t.estado, t.monto_final, t.motivo_cancel, " +
                "t.id_paciente, t.id_medico, t.id_consultorio, t.id_tipo_estudio, " +
                "pac.nombre AS pac_nombre, pac.apellido AS pac_apellido, " +
                "med.nombre AS med_nombre, med.apellido AS med_apellido, " +
                "te.nombre AS estudio, c.numero AS consultorio " +
                "FROM turno t " +
                "JOIN usuario pac ON t.id_paciente = pac.id_usuario " +
                "JOIN usuario med ON t.id_medico = med.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "JOIN consultorio c ON t.id_consultorio = c.id_consultorio ";
    }

    private List<Turno> ejecutarLista(String sql, int idParam) {
        List<Turno> lista = new ArrayList<>();
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (idParam > 0) {
                ps.setInt(1, idParam);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        }
        return lista;
    }

    private Turno mapear(ResultSet rs) throws SQLException {
        Turno t = new Turno();
        t.setId(rs.getInt("id_turno"));
        t.setFecha(rs.getDate("fecha").toLocalDate());
        Time h = rs.getTime("hora");
        if (h != null) t.setHora(h.toLocalTime());
        t.setEstado(EstadoTurno.valueOf(rs.getString("estado")));
        t.setMontoFinal(rs.getDouble("monto_final"));
        t.setMotivoCancel(rs.getString("motivo_cancel"));
        t.setIdPaciente(rs.getInt("id_paciente"));
        t.setIdMedico(rs.getInt("id_medico"));
        t.setIdConsultorio(rs.getInt("id_consultorio"));
        t.setIdTipoEstudio(rs.getInt("id_tipo_estudio"));
        t.setNombrePaciente(rs.getString("pac_nombre") + " " + rs.getString("pac_apellido"));
        t.setNombreMedico(rs.getString("med_nombre") + " " + rs.getString("med_apellido"));
        t.setNombreEstudio(rs.getString("estudio"));
        t.setNumeroConsultorio(rs.getString("consultorio"));
        return t;
    }
}
