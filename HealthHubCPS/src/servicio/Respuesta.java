package servicio;


public class Respuesta {
    private final boolean exito;
    private final String mensaje;

    private Respuesta(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public static Respuesta ok(String mensaje) {
        return new Respuesta(true, mensaje);
    }

    public static Respuesta error(String mensaje) {
        return new Respuesta(false, mensaje);
    }

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
}
