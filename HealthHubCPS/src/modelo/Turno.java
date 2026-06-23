package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoTurno estado;
    private double montoFinal;
    private int idPaciente;
    private int idMedico;
    private int idConsultorio;
    private int idTipoEstudio;
    private String motivoCancel;

    private String nombrePaciente;
    private String nombreMedico;
    private String nombreEstudio;
    private String numeroConsultorio;

    public Turno() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(double montoFinal) { this.montoFinal = montoFinal; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public int getIdConsultorio() { return idConsultorio; }
    public void setIdConsultorio(int idConsultorio) { this.idConsultorio = idConsultorio; }

    public int getIdTipoEstudio() { return idTipoEstudio; }
    public void setIdTipoEstudio(int idTipoEstudio) { this.idTipoEstudio = idTipoEstudio; }

    public String getMotivoCancel() { return motivoCancel; }
    public void setMotivoCancel(String motivoCancel) { this.motivoCancel = motivoCancel; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public String getNombreEstudio() { return nombreEstudio; }
    public void setNombreEstudio(String nombreEstudio) { this.nombreEstudio = nombreEstudio; }

    public String getNumeroConsultorio() { return numeroConsultorio; }
    public void setNumeroConsultorio(String numeroConsultorio) { this.numeroConsultorio = numeroConsultorio; }
}
