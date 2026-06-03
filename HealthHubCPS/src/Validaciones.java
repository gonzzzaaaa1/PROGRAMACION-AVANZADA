import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;


public class Validaciones {

    // Nombre y apellido:
    private static final String REGEX_NOMBRE = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' -]{2,60}$";

    // Email:
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // Telefono:
    private static final String REGEX_TELEFONO = "^[0-9+\\-() ]{6,30}$";

    // DNI:
    private static final String REGEX_DNI = "^[0-9]{7,8}$";

    // Matricula:
    private static final String REGEX_MATRICULA = "^[A-Za-z0-9-]{3,30}$";

    //Hora:
    private static final String REGEX_HORA = "^([01][0-9]|2[0-3]):[0-5][0-9]$";


    public static String validarNombre(String valor, String etiqueta) {
        if (valor == null || valor.trim().isEmpty()) {
            return etiqueta + " no puede estar vacio.";
        }
        if (!valor.trim().matches(REGEX_NOMBRE)) {
            return etiqueta + " solo puede contener letras, espacios, apostrofes o guiones " +
                    "(sin numeros ni simbolos). Entre 2 y 60 caracteres.";
        }
        return null;
    }

    public static String validarDni(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El DNI no puede estar vacio.";
        }
        if (!valor.trim().matches(REGEX_DNI)) {
            return "El DNI debe contener solo numeros (7 u 8 digitos).";
        }
        return null;
    }

    public static String validarContrasenia(String valor) {
        if (valor == null || valor.isEmpty()) {
            return "La contrasenia no puede estar vacia.";
        }
        if (valor.length() < 6) {
            return "La contrasenia debe tener al menos 6 caracteres.";
        }
        if (valor.length() > 100) {
            return "La contrasenia no puede tener mas de 100 caracteres.";
        }
        return null;
    }

    public static String validarEmail(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El email no puede estar vacio.";
        }
        if (!valor.trim().matches(REGEX_EMAIL)) {
            return "El email no tiene un formato valido (ej: nombre@dominio.com).";
        }
        if (valor.trim().length() > 120) {
            return "El email no puede tener mas de 120 caracteres.";
        }
        return null;
    }

    /** Telefono opcional: cadena vacia tambien es valida. */
    public static String validarTelefono(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null; // opcional
        }
        if (!valor.trim().matches(REGEX_TELEFONO)) {
            return "El telefono solo puede contener numeros, espacios y los simbolos + - ( ). " +
                    "Entre 6 y 30 caracteres.";
        }
        return null;
    }

    public static String validarDomicilio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El domicilio no puede estar vacio.";
        }
        if (valor.trim().length() > 150) {
            return "El domicilio no puede tener mas de 150 caracteres.";
        }
        return null;
    }

    public static String validarMatricula(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La matricula no puede estar vacia.";
        }
        if (!valor.trim().matches(REGEX_MATRICULA)) {
            return "La matricula solo puede contener letras, numeros y guiones (3 a 30 caracteres).";
        }
        return null;
    }

    /**
     * Valida fecha de nacimiento:
     * Devuelve null si esta OK, mensaje de error si no.
     */
    public static String validarFechaNacimiento(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La fecha de nacimiento no puede estar vacia.";
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(valor.trim());
        } catch (Exception e) {
            return "Formato de fecha invalido. Use YYYY-MM-DD.";
        }
        LocalDate hoy = LocalDate.now();
        if (fecha.isAfter(hoy)) {
            return "La fecha de nacimiento no puede ser futura.";
        }
        int edad = Period.between(fecha, hoy).getYears();
        if (edad > 120) {
            return "La fecha de nacimiento no es razonable (edad mayor a 120 anios).";
        }
        return null;
    }

    /**
     * Valida tarifa de tipo de estudio: numero >= 0.
     */
    public static String validarTarifa(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La tarifa no puede estar vacia.";
        }
        try {
            double tarifa = Double.parseDouble(valor.trim());
            if (tarifa < 0) {
                return "La tarifa debe ser un numero positivo.";
            }
        } catch (NumberFormatException e) {
            return "La tarifa debe ser un numero valido.";
        }
        return null;
    }

    /**
     * Valida porcentaje de cobertura: numero entre 0 y 99.99.
     */
    public static String validarPorcentajeCobertura(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El porcentaje no puede estar vacio.";
        }
        try {
            double porc = Double.parseDouble(valor.trim());
            if (porc < 0 || porc > 99.99) {
                return "El porcentaje debe estar entre 0 y 99.99.";
            }
        } catch (NumberFormatException e) {
            return "El porcentaje debe ser un numero valido.";
        }
        return null;
    }
    
    /**
     * Valida la fecha de un turno: formato YYYY-MM-DD, no anterior a hoy
     * y no mas alla de un anio en el futuro.
     */
    public static String validarFechaTurno(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La fecha del turno no puede estar vacia.";
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(valor.trim());
        } catch (Exception e) {
            return "Formato de fecha invalido. Use YYYY-MM-DD.";
        }
        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) {
            return "La fecha del turno no puede ser anterior a hoy.";
        }
        if (fecha.isAfter(hoy.plusYears(1))) {
            return "La fecha del turno no puede ser mayor a un anio a futuro.";
        }
        return null;
    }

    /**
     * Valida duracion en minutos de un tipo de estudio: entero > 0.
     */
    public static String validarDuracionMinutos(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La duracion no puede estar vacia.";
        }
        try {
            int minutos = Integer.parseInt(valor.trim());
            if (minutos <= 0) {
                return "La duracion debe ser un numero entero positivo.";
            }
        } catch (NumberFormatException e) {
            return "La duracion debe ser un numero entero valido.";
        }
        return null;
    }

    /**
     * Valida la hora de un turno: formato HH:mm valido (00:00 a 23:59).
     */
    public static String validarHoraTurno(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "La hora no puede estar vacia.";
        }
        if (!valor.trim().matches(REGEX_HORA)) {
            return "Formato de hora invalido. Use HH:mm (ej: 09:30, 14:45).";
        }
        return null;
    }

    public static String validarHoraFutura(LocalDate fecha, String hora) {
        if (fecha == null || hora == null) return null;
        if (!fecha.equals(LocalDate.now())) return null;
        try {
            LocalTime horaTurno = LocalTime.parse(hora.trim());
            if (!horaTurno.isAfter(LocalTime.now())) {
                return "La hora del turno ya paso. Elija una hora futura.";
            }
        } catch (Exception e) {
            return "Formato de hora invalido.";
        }
        return null;
    }

    public static String validarMotivoCancelacion(String valor) {
        if (valor == null) return null; // opcional
        if (valor.trim().length() > 100) {
            return "El motivo de cancelacion no puede tener mas de 100 caracteres.";
        }
        return null;
    }
}