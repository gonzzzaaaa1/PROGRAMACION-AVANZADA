public class Usuario {
    private int id;
    private String dni;
    private String nombre;
    private String contrasenia;
    private String rol;


    public Usuario() {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getDni() {return dni;}
    public void setDni(String dni) {this.dni = dni;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getContrasenia() {return contrasenia;}
    public void setContrasenia(String contrasenia) {this.contrasenia = contrasenia;}

    public String getRol() {return rol;}
    public void setRol(String rol) {this.rol = rol;}
}
