package modelo;

public class Medico extends Usuario {
    private String matricula;
    private int idEspecialidad;

    public Medico() {
        super();
        setRol(Rol.MEDICO);
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
}
