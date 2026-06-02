public class UsuarioValidacion {

    public static String validarDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return "El DNI no puede estar vacio";
        }
        if (!dni.trim().matches("\\d+")) {
            return "El DNI solo puede contener numeros";
        }
        int largo = dni.trim().length();
        if (largo < 7 || largo > 8) {
            return "El DNI debe tener entre 7 y 8 digitos";
        }
        return null;
    }

    public static String validarContrasenia(String contrasenia) {
        if (contrasenia == null || contrasenia.trim().isEmpty()) {
            return "La contrasenia no puede estar vacia";
        }
        if (contrasenia.length() < 6) {
            return "La contrasenia debe tener al menos 6 caracteres";
        }
        return null;
    }

    public static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacio";
        }
        if (!nombre.trim().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            return "El nombre solo puede contener letras";
        }
        return null;
    }

    public static String validarApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            return "El apellido no puede estar vacio";
        }
        if (!apellido.trim().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            return "El apellido solo puede contener letras";
        }
        return null;
    }

    public static String validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El email no puede estar vacio";
        }
        if (!email.trim().contains("@") || !email.trim().contains(".")) {
            return "El email no tiene un formato valido";
        }
        return null;
    }

    public static String validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "El telefono no puede estar vacio";
        }
        if (!telefono.trim().matches("[0-9+\\-() ]+")) {
            return "El telefono solo puede contener numeros y los simbolos + - ( )";
        }
        return null;
    }

    public static String validarRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return "El rol no puede estar vacio";
        }
        String rolUpper = rol.trim().toUpperCase();
        if (!rolUpper.equals("ADMIN") && !rolUpper.equals("MEDICO") && !rolUpper.equals("PACIENTE")) {
            return "El rol debe ser ADMIN, MEDICO o PACIENTE";
        }
        return null;
    }
}
