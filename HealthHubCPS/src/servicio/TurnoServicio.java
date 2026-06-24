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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TurnoServicio {


    public static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);

    public static final LocalTime HORA_CIERRE = LocalTime.of(17, 0);

    public static final int INTERVALO_DEFECTO = 30;

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final ConsultorioDAO consultorioDAO = new ConsultorioDAO();
    private final TipoEstudioDAO tipoEstudioDAO = new TipoEstudioDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final CoberturaDAO coberturaDAO = new CoberturaDAO();


    public List<LocalTime> horariosDisponibles(LocalDate fecha, int idMedico, int idTipoEstudio) {
        List<LocalTime> disponibles = new ArrayList<>();
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            return disponibles;
        }

        int intervalo = INTERVALO_DEFECTO;
        TipoEstudio te = tipoEstudioDAO.buscarPorId(idTipoEstudio);
        if (te != null && te.getTiempoMinutos() > 0) {
            intervalo = te.getTiempoMinutos();
        }


        Set<LocalTime> ocupadasMedico = new HashSet<>(
                turnoDAO.listarHorasOcupadasMedico(idMedico, fecha));

        int totalConsultorios = consultorioDAO.listarTodos().size();
        boolean esHoy = fecha.isEqual(LocalDate.now());
        LocalTime ahora = LocalTime.now();

        LocalTime hora = HORA_APERTURA;
        while (hora.isBefore(HORA_CIERRE)) {
            boolean libre = true;

            if (esHoy && !hora.isAfter(ahora)) libre = false;

            if (libre && ocupadasMedico.contains(hora)) libre = false;

            if (libre && totalConsultorios > 0
                    && turnoDAO.contarConsultoriosOcupados(fecha, hora) >= totalConsultorios) {
                libre = false;
            }

            if (libre) disponibles.add(hora);
            hora = hora.plusMinutes(intervalo);
        }
        return disponibles;
    }


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
        if (fecha == null || hora == null) {
            return Respuesta.error("Tenes que elegir fecha y horario.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            return Respuesta.error("La fecha del turno no puede ser anterior a hoy.");
        }

        if (hora.isBefore(HORA_APERTURA) || !hora.isBefore(HORA_CIERRE)) {
            return Respuesta.error("El horario esta fuera del horario de atencion (" +
                    HORA_APERTURA + " a " + HORA_CIERRE + ").");
        }

        if (fecha.isEqual(LocalDate.now()) && !hora.isAfter(LocalTime.now())) {
            return Respuesta.error("Ese horario ya paso. Elegi uno posterior.");
        }

        if (turnoDAO.medicoOcupado(idMedico, fecha, hora)) {
            return Respuesta.error("Ese horario con el medico ya fue ocupado. Elegi otro.");
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

        return Respuesta.error("No se pudo agendar el turno. Es posible que el horario " +
                "se haya ocupado recien. Actualiza los horarios e intenta de nuevo.");
    }


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