package modelo;

import java.time.LocalDate;

public class Paciente extends Usuario {
    private LocalDate fechaNacimiento;
    private String domicilio;
    private int idObraSocial;

    public Paciente() {
        super();
        setRol(Rol.PACIENTE);
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public int getIdObraSocial() { return idObraSocial; }
    public void setIdObraSocial(int idObraSocial) { this.idObraSocial = idObraSocial; }
}
