import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoController {

    /**
     * Lista los turnos agendados del médico (su agenda).
     */
    public List<String> listarAgenda(int idMedico) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT t.id_turno, t.fecha, t.hora, u.nombre, u.apellido, " +
                "te.nombre AS tipo_estudio, con.numero " +
                "FROM turno t " +
                "JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "JOIN consultorio con ON t.id_consultorio = con.id_consultorio " +
                "WHERE t.id_medico = ? AND t.estado = 'AGENDADO' " +
                "ORDER BY t.fecha, t.hora";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_turno");
                    String fecha = rs.getString("fecha");
                    String hora = rs.getString("hora");
                    String paciente = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("tipo_estudio");
                    String consultorio = rs.getString("numero");

                    lista.add(id + " - " + fecha + " " + hora + " | Paciente: " + paciente +
                            " | Estudio: " + estudio + " | Consultorio: " + consultorio);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar la agenda del médico: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los pacientes que tienen (o tuvieron) turnos con el médico.
     */
    public List<String> listarPacientesDelMedico(int idMedico) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT u.id_usuario, u.nombre, u.apellido " +
                "FROM turno t " +
                "JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "WHERE t.id_medico = ? " +
                "ORDER BY u.apellido, u.nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    lista.add(id + " - " + nombre + " " + apellido);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pacientes del médico: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Devuelve el id_historia de un paciente, o -1 si no tiene historia clínica.
     */
    public int obtenerIdHistoria(int idPaciente) {
        String sql = "SELECT id_historia FROM historia_clinica WHERE id_paciente = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_historia");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historia clínica: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Devuelve la historia clínica del paciente (datos + archivos) como texto,
     * o null si el paciente no tiene historia.
     */
    public String verHistoriaClinica(int idPaciente) {
        StringBuilder sb = new StringBuilder();
        Connection con = Conexion.getInstance().getConnection();

        String sqlHistoria = "SELECT h.fecha_creacion, h.observaciones, u.nombre, u.apellido " +
                "FROM historia_clinica h " +
                "JOIN usuario u ON h.id_paciente = u.id_usuario " +
                "WHERE h.id_paciente = ?";

        try (PreparedStatement ps = con.prepareStatement(sqlHistoria)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String paciente = rs.getString("nombre") + " " + rs.getString("apellido");
                String observaciones = rs.getString("observaciones");
                if (observaciones == null || observaciones.isEmpty()) {
                    observaciones = "(sin observaciones)";
                }
                sb.append("Paciente: ").append(paciente).append("\n");
                sb.append("Fecha de creación: ").append(rs.getString("fecha_creacion")).append("\n");
                sb.append("Observaciones: ").append(observaciones).append("\n");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener historia clínica: " + e.getMessage());
            return null;
        }

        sb.append("\nARCHIVOS ADJUNTOS:\n");
        String sqlArchivos = "SELECT a.tipo, a.formato, a.url, a.fecha_carga " +
                "FROM archivo_adjunto a " +
                "JOIN historia_clinica h ON a.id_historia = h.id_historia " +
                "WHERE h.id_paciente = ? " +
                "ORDER BY a.fecha_carga DESC";

        try (PreparedStatement ps = con.prepareStatement(sqlArchivos)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hayArchivos = false;
                while (rs.next()) {
                    hayArchivos = true;
                    sb.append("- [").append(rs.getString("tipo")).append("/").append(rs.getString("formato"))
                            .append("] ").append(rs.getString("url"))
                            .append(" (").append(rs.getString("fecha_carga")).append(")\n");
                }
                if (!hayArchivos) {
                    sb.append("(sin archivos)\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar archivos: " + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Adjunta un archivo a la historia clínica.
     */
    public boolean adjuntarArchivo(int idHistoria, int idMedico, String tipo, String formato, String url) {
        String sql = "INSERT INTO archivo_adjunto (id_historia, id_medico_carga, tipo, formato, url, fecha_carga) " +
                "VALUES (?, ?, ?::tipo_archivo, ?::formato_archivo, ?, CURRENT_DATE)";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHistoria);
            ps.setInt(2, idMedico);
            ps.setString(3, tipo);
            ps.setString(4, formato);
            ps.setString(5, url);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al adjuntar archivo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista los resultados de los turnos del médico que todavía no están autorizados.
     */
    public List<String> listarResultadosPendientes(int idMedico) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT r.id_resultado, t.fecha, u.nombre, u.apellido, " +
                "te.nombre AS tipo_estudio, r.descripcion " +
                "FROM resultado r " +
                "JOIN turno t ON r.id_turno = t.id_turno " +
                "JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "WHERE t.id_medico = ? AND r.autorizado = FALSE " +
                "ORDER BY t.fecha";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_resultado");
                    String paciente = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("tipo_estudio");
                    String descripcion = rs.getString("descripcion");

                    lista.add(id + " - Paciente: " + paciente + " | Estudio: " + estudio +
                            " | " + descripcion);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar resultados pendientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista los turnos del médico que todavía no tienen resultado cargado.
     */
    public List<String> listarTurnosSinResultado(int idMedico) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT t.id_turno, t.fecha, t.hora, u.nombre, u.apellido, te.nombre AS tipo_estudio " +
                "FROM turno t " +
                "JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "WHERE t.id_medico = ? AND t.estado != 'CANCELADO' " +
                "AND t.id_turno NOT IN (SELECT id_turno FROM resultado) " +
                "ORDER BY t.fecha DESC, t.hora DESC";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_turno");
                    String fecha = rs.getString("fecha");
                    String hora = rs.getString("hora");
                    String paciente = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("tipo_estudio");

                    lista.add(id + " - " + fecha + " " + hora + " | " + paciente + " | " + estudio);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos sin resultado: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Sube un resultado a un turno. Si autorizar=true lo deja visible al paciente de inmediato.
     */
    public boolean subirResultado(int idTurno, String descripcion, boolean autorizar, int idMedico) {
        String sql;
        if (autorizar) {
            sql = "INSERT INTO resultado (id_turno, descripcion, autorizado, fecha_autorizacion, id_medico_autoriza) " +
                    "VALUES (?, ?, TRUE, CURRENT_DATE, ?)";
        } else {
            sql = "INSERT INTO resultado (id_turno, descripcion, autorizado) VALUES (?, ?, FALSE)";
        }

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTurno);
            ps.setString(2, descripcion);
            if (autorizar) {
                ps.setInt(3, idMedico);
            }

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al subir resultado: " + e.getMessage());
        }
        return false;
    }

    /**
     * Autoriza un resultado para que el paciente pueda verlo.
     */
    public boolean autorizarResultado(int idResultado, int idMedico) {
        String sql = "UPDATE resultado " +
                "SET autorizado = TRUE, fecha_autorizacion = CURRENT_DATE, id_medico_autoriza = ? " +
                "WHERE id_resultado = ? AND autorizado = FALSE";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idResultado);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al autorizar resultado: " + e.getMessage());
        }
        return false;
    }
}
