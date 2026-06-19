package modelo;

public class TipoEstudio {
    private int id;
    private String nombre;
    private Complejidad complejidad;
    private int tiempoMinutos;
    private double tarifaBase;

    public TipoEstudio() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Complejidad getComplejidad() { return complejidad; }
    public void setComplejidad(Complejidad complejidad) { this.complejidad = complejidad; }

    public int getTiempoMinutos() { return tiempoMinutos; }
    public void setTiempoMinutos(int tiempoMinutos) { this.tiempoMinutos = tiempoMinutos; }

    public double getTarifaBase() { return tarifaBase; }
    public void setTarifaBase(double tarifaBase) { this.tarifaBase = tarifaBase; }

    @Override
    public String toString() { return nombre; }
}
