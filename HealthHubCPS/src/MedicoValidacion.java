public class MedicoValidacion {

    public static String validarMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return "La matricula no puede estar vacia";
        }
        if (matricula.trim().length() > 30) {
            return "La matricula no puede superar 30 caracteres";
        }
        return null;
    }

    public static String validarIdEspecialidad(int idEspecialidad) {
        if (idEspecialidad <= 0) {
            return "Debe seleccionar una especialidad valida";
        }
        return null;
    }

    public static String validarIdUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            return "El id de usuario no es valido";
        }
        return null;
    }
}
