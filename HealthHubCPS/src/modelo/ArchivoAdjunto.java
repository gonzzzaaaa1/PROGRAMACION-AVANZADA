package modelo;

import java.time.LocalDate;

public class ArchivoAdjunto {
    private int id;
    private int idHistoria;
    private int idMedicoCarga;
    private TipoArchivo tipo;
    private FormatoArchivo formato;
    private String url;
    private LocalDate fechaCarga;

    public ArchivoAdjunto() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdHistoria() { return idHistoria; }
    public void setIdHistoria(int idHistoria) { this.idHistoria = idHistoria; }

    public int getIdMedicoCarga() { return idMedicoCarga; }
    public void setIdMedicoCarga(int idMedicoCarga) { this.idMedicoCarga = idMedicoCarga; }

    public TipoArchivo getTipo() { return tipo; }
    public void setTipo(TipoArchivo tipo) { this.tipo = tipo; }

    public FormatoArchivo getFormato() { return formato; }
    public void setFormato(FormatoArchivo formato) { this.formato = formato; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDate getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDate fechaCarga) { this.fechaCarga = fechaCarga; }
}
