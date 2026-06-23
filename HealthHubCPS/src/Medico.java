public class Medico {
    private int idUsuario;
    private String matricula;
    private int idEspecialidad;
    private String nombre;
    private String apellido;
    private String especialidadNombre;

    // Constructor
    public Medico() {
    }

    // Constructor con todos los datos
    public Medico(int idUsuario, String matricula, int idEspecialidad,
                  String nombre, String apellido, String especialidadNombre) {
        this.idUsuario = idUsuario;
        this.matricula = matricula;
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidadNombre = especialidadNombre;
    }

    // Getters y setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEspecialidadNombre() { return especialidadNombre; }
    public void setEspecialidadNombre(String especialidadNombre) { this.especialidadNombre = especialidadNombre; }

    @Override
    public String toString() {
        return "Medico{" +
                "idUsuario=" + idUsuario +
                ", matricula='" + matricula + '\'' +
                ", nombre='" + nombre + " " + apellido + '\'' +
                ", especialidad='" + especialidadNombre + '\'' +
                '}';
    }
}
