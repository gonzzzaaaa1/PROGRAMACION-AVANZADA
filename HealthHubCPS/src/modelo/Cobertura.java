package modelo;

/** Asociacion entre ObraSocial y TipoEstudio (clave compuesta). */
public class Cobertura {
    private int idObraSocial;
    private int idTipoEstudio;
    private double porcentajeCobertura;
    private boolean vigente;

    private String nombreObraSocial;
    private String nombreTipoEstudio;

    public Cobertura() {
    }

    public int getIdObraSocial() { return idObraSocial; }
    public void setIdObraSocial(int idObraSocial) { this.idObraSocial = idObraSocial; }

    public int getIdTipoEstudio() { return idTipoEstudio; }
    public void setIdTipoEstudio(int idTipoEstudio) { this.idTipoEstudio = idTipoEstudio; }

    public double getPorcentajeCobertura() { return porcentajeCobertura; }
    public void setPorcentajeCobertura(double porcentajeCobertura) { this.porcentajeCobertura = porcentajeCobertura; }

    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }

    public String getNombreObraSocial() { return nombreObraSocial; }
    public void setNombreObraSocial(String nombreObraSocial) { this.nombreObraSocial = nombreObraSocial; }

    public String getNombreTipoEstudio() { return nombreTipoEstudio; }
    public void setNombreTipoEstudio(String nombreTipoEstudio) { this.nombreTipoEstudio = nombreTipoEstudio; }
}
