import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        String[] menu = {"Login", "Salir"};

        String[] opPaciente = {
                "Completar Datos",
                "Actualizar Datos Personales",
                "Solicitar Turno",
                "Ver Mis Turnos",
                "Cancelar Turno",
                "Ver Resultados de Estudios",
                "Cerrar Sesion"
        };

        String[] opMedico = {
                "Ver Agenda",
                "Ver Historia Clinica",
                "Adjuntar Archivo a Historia Clinica",
                "Autorizar Resultados",
                "Cancelar Turno",
                "Cerrar Sesion"
        };

        String[] opAdmin = {
                "Registrar Usuario",
                "Dar de Baja Usuario",
                "Asignar Consultorio a Medico",
                "Configurar Tiempos de Turno",
                "Configurar Tarifas de Consulta y coberturas",
                "Cerrar Sesion"
        };

        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido a HealthHubCPS",
                    "Sistema de Gestion",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    menu,
                    menu[0]
            );

            if (opcion == 0) {
                String dni = JOptionPane.showInputDialog(null, "DNI:");
                if (dni == null) continue;

                String contrasenia = JOptionPane.showInputDialog(null, "Contrasenia:");
                if (contrasenia == null) continue;

                UsuarioController controller = new UsuarioController();
                Usuario user = controller.validarLogin(dni, contrasenia);

                if (user == null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Credenciales incorrectas o usuario inactivo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    switch (user.getRol().toUpperCase()) {
                        case "ADMIN":
                            menuInterno("Admin", opAdmin);
                            break;
                        case "MEDICO":
                            menuInterno("Medico", opMedico);
                            break;
                        case "PACIENTE":
                            menuInterno("Paciente", opPaciente);
                            break;
                        default:
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Rol desconocido: " + user.getRol(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                    }
                }
            }
        } while (opcion != 1 && opcion != JOptionPane.CLOSED_OPTION);
    }

    public static void menuInterno(String rol, String[] opciones) {
        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Panel de " + rol,
                    "HealthHubCPS",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion != JOptionPane.CLOSED_OPTION && seleccion != opciones.length - 1) {
                JOptionPane.showMessageDialog(
                        null,
                        "Has seleccionado: " + opciones[seleccion]
                );
            }
        } while (seleccion != opciones.length - 1 && seleccion != JOptionPane.CLOSED_OPTION);
    }
}