package util;

import java.time.LocalDate;
import java.time.LocalTime;

/** Validaciones y conversiones de datos de entrada usadas por la interfaz. */
public class Validaciones {

    public static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean soloDigitos(String s) {
        return s != null && s.matches("\\d+");
    }

    public static boolean emailValido(String s) {
        return s != null && s.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    /** Convierte "AAAA-MM-DD" a LocalDate, o null si el formato es invalido. */
    public static LocalDate parsearFecha(String s) {
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** Convierte "HH:MM" a LocalTime, o null si el formato es invalido. */
    public static LocalTime parsearHora(String s) {
        try {
            return LocalTime.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer parsearEntero(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Double parsearDecimal(String s) {
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }
}
