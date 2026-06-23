package servicio;

import dao.CoberturaDAO;
import dao.ConsultorioDAO;
import dao.PacienteDAO;
import dao.TipoEstudioDAO;
import dao.TurnoDAO;
import modelo.Consultorio;
import modelo.EstadoTurno;
import modelo.Paciente;
import modelo.TipoEstudio;
import modelo.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Logica de turnos: agendar (con consultorio y monto), listar y cancelar. */
public class TurnoServicio {

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final ConsultorioDAO consultorioDAO = new ConsultorioDAO();
    private final TipoEstudioDAO tipoEstudioDAO = new TipoEstudioDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final CoberturaDAO coberturaDAO = new CoberturaDAO();

    /**
     * Calcula el monto final del turno: tarifa base del estudio menos el porcentaje
     * de cobertura vigente de la obra social del paciente.
     */
    public double calcularMonto(int idPaciente, int idTipoEstudio) {
        TipoEstudio te = tipoEstudioDAO.buscarPorId(idTipoEstudio);
        if (te == null) {
            return 0;
        }
        double tarifa = te.getTarifaBase();
        Paciente p = pacienteDAO.buscarPorId(idPaciente);
        double porcentaje = 0;
        if (p != null && p.getIdObraSocial() > 0) {
            porcentaje = coberturaDAO.obtenerPorcentajeVigente(p.getIdObraSocial(), idTipoEstudio);
        }
        double monto = tarifa * (1 - porcentaje / 100.0);
        if (monto < 0) monto = 0;
        // Redondeo a 2 decimales.
        return Math.round(monto * 100.0) / 100.0;
    }

    /**
     * Agenda un turno: verifica que el paciente tenga datos, busca un consultorio libre,
     * calcula el monto y lo guarda.
     */
    public Respuesta solicitarTurno(int idPaciente, LocalDate fecha, LocalTime hora,
                                    int idMedico, int idTipoEstudio) {
        if (!pacienteDAO.tieneDatos(idPaciente)) {
            return Respuesta.error("Primero tenes que completar tus datos de paciente.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            return Respuesta.error("La fecha del turno no puede ser anterior a hoy.");
        }
        Consultorio libre = consultorioDAO.buscarLibre(fecha, hora);
        if (libre == null) {
            return Respuesta.error("No hay consultorios disponibles en esa fecha y horario.");
        }
        double monto = calcularMonto(idPaciente, idTipoEstudio);

        Turno t = new Turno();
        t.setFecha(fecha);
        t.setHora(hora);
        t.setEstado(EstadoTurno.AGENDADO);
        t.setMontoFinal(monto);
        t.setIdPaciente(idPaciente);
        t.setIdMedico(idMedico);
        t.setIdConsultorio(libre.getId());
        t.setIdTipoEstudio(idTipoEstudio);

        if (turnoDAO.insertar(t)) {
            return Respuesta.ok("Turno agendado en " + libre + ". Monto final: $" + monto);
        }
        return Respuesta.error("No se pudo agendar el turno.");
    }

    /** Cancela un turno con su motivo. */
    public Respuesta cancelarTurno(int idTurno, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            return Respuesta.error("Tenes que indicar un motivo de cancelacion.");
        }
        if (turnoDAO.cancelar(idTurno, motivo)) {
            return Respuesta.ok("Turno cancelado.");
        }
        return Respuesta.error("No se pudo cancelar el turno.");
    }

    public List<Turno> listarTurnosPaciente(int idPaciente) {
        return turnoDAO.listarPorPaciente(idPaciente);
    }

    public List<Turno> listarTurnosActivosPaciente(int idPaciente) {
        return turnoDAO.listarActivosPaciente(idPaciente);
    }

    public List<Turno> listarAgendaMedico(int idMedico) {
        return turnoDAO.listarAgendaMedico(idMedico);
    }
}
