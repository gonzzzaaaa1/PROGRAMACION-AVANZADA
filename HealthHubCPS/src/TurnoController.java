import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TurnoController {

    public List<String> listarEspecialidades() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_especialidad, nombre FROM especialidad ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_especialidad");
                String nombre = rs.getString("nombre");
                lista.add(id + " - " + nombre);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar especialidades: " + e.getMessage());
        }
        return lista;
    }

    public List<String> listarMedicosPorEspecialidad(int idEspecialidad) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, m.matricula " +
                "FROM usuario u " +
                "JOIN medico m ON u.id_usuario = m.id_usuario " +
                "WHERE m.id_especialidad = ? AND u.activo = TRUE " +
                "ORDER BY u.apellido, u.nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String matricula = rs.getString("matricula");
                    lista.add(id + " - " + nombre + " " + apellido + " [" + matricula + "]");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar médicos: " + e.getMessage());
        }
        return lista;
    }

    public List<String> listarTiposEstudio() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_estudio, nombre, tarifa_base FROM tipo_estudio ORDER BY nombre";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_tipo_estudio");
                String nombre = rs.getString("nombre");
                double tarifa = rs.getDouble("tarifa_base");
                lista.add(id + " - " + nombre + " ($" + tarifa + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar tipos de estudio: " + e.getMessage());
        }
        return lista;
    }

    public String[] asignarConsultorioAutomatico(LocalDate fecha, String hora) {
        String sql = "SELECT c.id_consultorio, c.numero, c.ubicacion " +
                "FROM consultorio c " +
                "WHERE c.id_consultorio NOT IN ( " +
                "    SELECT t.id_consultorio FROM turno t " +
                "    WHERE t.fecha = ? AND t.hora = ?::TIME AND t.estado = 'AGENDADO' " +
                ") " +
                "ORDER BY c.numero " +
                "LIMIT 1";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setString(2, hora);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = String.valueOf(rs.getInt("id_consultorio"));
                    String ubicacion = rs.getString("ubicacion");
                    return new String[]{ id, ubicacion };
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al asignar consultorio: " + e.getMessage());
        }
        return null;
    }
    public double calcularMontoTurno(int idTipoEstudio, int idPaciente) {
        double montoFinal = 0;

        String sql = "SELECT ts.tarifa_base, COALESCE(c.porcentaje_cobertura, 0) AS cobertura " +
                "FROM tipo_estudio ts " +
                "JOIN paciente p ON p.id_usuario = ? " +
                "LEFT JOIN cobertura c ON c.id_tipo_estudio = ts.id_tipo_estudio " +
                "                     AND c.id_obra_social = p.id_obra_social " +
                "                     AND c.vigente = TRUE " +
                "WHERE ts.id_tipo_estudio = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setInt(2, idTipoEstudio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double tarifaBase = rs.getDouble("tarifa_base");
                    double porcentajeCobertura = rs.getDouble("cobertura");
                    montoFinal = tarifaBase * (1 - porcentajeCobertura / 100);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular monto: " + e.getMessage());
        }
        return montoFinal;
    }


    public int crearTurno(LocalDate fecha, String hora, int idPaciente, int idMedico,
                          int idConsultorio, int idTipoEstudio, double montoFinal) {
        String sql = "INSERT INTO turno (fecha, hora, estado, monto_final, id_paciente, " +
                "id_medico, id_consultorio, id_tipo_estudio) " +
                "VALUES (?, ?::TIME, 'AGENDADO'::estado_turno, ?, ?, ?, ?, ?) " +
                "RETURNING id_turno";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setString(2, hora);
            ps.setDouble(3, montoFinal);
            ps.setInt(4, idPaciente);
            ps.setInt(5, idMedico);
            ps.setInt(6, idConsultorio);
            ps.setInt(7, idTipoEstudio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_turno");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al crear turno: " + e.getMessage());
        }
        return -1;
    }

    public List<String> listarTurnosPaciente(int idPaciente) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT t.id_turno, t.fecha, t.hora, t.estado, u.nombre, u.apellido, " +
                "te.nombre as tipo_estudio, t.monto_final, con.numero " +
                "FROM turno t " +
                "JOIN paciente p ON t.id_paciente = p.id_usuario " +
                "JOIN usuario u ON t.id_medico = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "JOIN consultorio con ON t.id_consultorio = con.id_consultorio " +
                "WHERE p.id_usuario = ? " +
                "ORDER BY t.fecha DESC, t.hora DESC";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_turno");
                    String fecha = rs.getString("fecha");
                    String hora = rs.getString("hora");
                    String estado = rs.getString("estado");
                    String medico = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("tipo_estudio");
                    double monto = rs.getDouble("monto_final");
                    String consultorio = rs.getString("numero");

                    String turno = "ID: " + id + " | Fecha: " + fecha + " - Hora: " + hora +
                            " | Médico: " + medico + " | Estudio: " + estudio +
                            " | Consultorio: " + consultorio + " | Estado: " + estado +
                            " | Monto: $" + monto;
                    lista.add(turno);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos del paciente: " + e.getMessage());
        }
        return lista;
    }

    public List<String> listarTurnosActivos(int idPaciente) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT t.id_turno, t.fecha, t.hora, u.nombre, u.apellido, te.nombre " +
                "FROM turno t " +
                "JOIN paciente p ON t.id_paciente = p.id_usuario " +
                "JOIN usuario u ON t.id_medico = u.id_usuario " +
                "JOIN tipo_estudio te ON t.id_tipo_estudio = te.id_tipo_estudio " +
                "WHERE p.id_usuario = ? AND t.estado = 'AGENDADO' " +
                "ORDER BY t.fecha, t.hora";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_turno");
                    String fecha = rs.getString("fecha");
                    String hora = rs.getString("hora");
                    String medico = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("nombre");

                    lista.add(id + " - " + fecha + " " + hora + " | " + medico + " | " + estudio);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos activos: " + e.getMessage());
        }
        return lista;
    }

    public boolean cancelarTurno(int idTurno, String motivo) {
        String sql = "UPDATE turno SET estado = 'CANCELADO'::estado_turno, motivo_cancel = ? " +
                "WHERE id_turno = ?";

        Connection con = Conexion.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idTurno);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al cancelar turno: " + e.getMessage());
        }
        return false;
    }

    public List<String> listarResultadosPaciente(int idPaciente) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT r.id_resultado, t.fecha, u.nombre, u.apellido, te.nombre as tipo_estudio, " +
                "r.descripcion, r.fecha_autorizacion " +
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
                    String fecha = rs.getString("fecha");
                    String medico = rs.getString("nombre") + " " + rs.getString("apellido");
                    String estudio = rs.getString("tipo_estudio");
                    String descripcion = rs.getString("descripcion");
                    String fechaAutorizacion = rs.getString("fecha_autorizacion");

                    String resultado = "Estudio: " + estudio + " (Turno: " + fecha + ")\n" +
                            "Médico: " + medico + "\n" +
                            "Resultado: " + descripcion + "\n" +
                            "Autorizado: " + fechaAutorizacion;
                    lista.add(resultado);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar resultados: " + e.getMessage());
        }
        return lista;
    }
}