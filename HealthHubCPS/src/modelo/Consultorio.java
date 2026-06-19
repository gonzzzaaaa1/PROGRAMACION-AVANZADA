package modelo;

public class Consultorio {
    private int id;
    private String numero;
    private String ubicacion;

    public Consultorio() {
    }

    public Consultorio(int id, String numero, String ubicacion) {
        this.id = id;
        this.numero = numero;
        this.ubicacion = ubicacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String toString() { return "Consultorio " + numero + " - " + ubicacion; }
}
