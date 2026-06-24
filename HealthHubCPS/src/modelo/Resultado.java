package modelo;

import java.time.LocalDate;

public class Resultado {
    private int id;
    private int idTurno;
    private String descripcion;
    private boolean autorizado;
    private LocalDate fechaAutorizacion;
    private int idMedicoAutoriza;

    private String nombrePaciente;
    private String nombreMedico;
    private String nombreEstudio;
    private String fechaTurno;

    public Resultado() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isAutorizado() { return autorizado; }
    public void setAutorizado(boolean autorizado) { this.autorizado = autorizado; }

    public LocalDate getFechaAutorizacion() { return fechaAutorizacion; }
    public void setFechaAutorizacion(LocalDate fechaAutorizacion) { this.fechaAutorizacion = fechaAutorizacion; }

    public int getIdMedicoAutoriza() { return idMedicoAutoriza; }
    public void setIdMedicoAutoriza(int idMedicoAutoriza) { this.idMedicoAutoriza = idMedicoAutoriza; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public String getNombreEstudio() { return nombreEstudio; }
    public void setNombreEstudio(String nombreEstudio) { this.nombreEstudio = nombreEstudio; }

    public String getFechaTurno() { return fechaTurno; }
    public void setFechaTurno(String fechaTurno) { this.fechaTurno = fechaTurno; }
}
