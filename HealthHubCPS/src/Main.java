import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] menu = {"Login", "Salir"};

        String[] opPaciente = {
                "Completar Datos", "Actualizar Datos Personales", "Solicitar Turno",
                "Ver Mis Turnos", "Cancelar Turno", "Ver Resultados de Estudios", "Cerrar Sesion"
        };

        String[] opMedico = {
                "Ver Agenda", "Ver Historia Clinica", "Adjuntar Archivo a Historia Clinica",
                "Autorizar Resultados", "Cancelar Turno", "Cerrar Sesion"
        };

        String[] opAdmin = {
                "Registrar Usuario", "Dar de Baja Usuario", "Asignar Consultorio a Medico",
                "Configurar Tiempos de Turno", "Configurar Tarifas de Consulta y coberturas", "Cerrar Sesion"
        };

        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, "Bienvenido a HealthHubCPS",
                    "Sistema de Gestion", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, menu, menu[0]);

            if (opcion == 0) {
                String dni = JOptionPane.showInputDialog(null, "DNI:");
                if (dni == null) continue;

                String contrasenia = JOptionPane.showInputDialog(null, "Contrasenia:");
                if (contrasenia == null) continue;

                UsuarioController controller = new UsuarioController();
                Usuario user = controller.validarLogin(dni, contrasenia);

                if (user == null) {
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas o usuario inactivo",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    switch (user.getRol().toUpperCase()) {
                        case "ADMIN":
                            menuAdmin(opAdmin);
                            break;
                        case "MEDICO":
                            menuInterno("Medico", opMedico);
                            break;
                        case "PACIENTE":
                            menuInterno("Paciente", opPaciente);
                            break;
                    }
                }
            }
        } while (opcion != 1 && opcion != JOptionPane.CLOSED_OPTION);
    }

    /**
     * Menú del admin con las funciones implementadas.
     */
    public static void menuAdmin(String[] opciones) {
        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(null, "Panel de Admin",
                    "HealthHubCPS", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION) break;

            switch (seleccion) {
                case 0:
                    registrarUsuario();
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "Funcion pendiente: Dar de Baja Usuario");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Funcion pendiente: Asignar Consultorio");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Funcion pendiente: Configurar Tiempos");
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "Funcion pendiente: Configurar Tarifas");
                    break;
                case 5:
                    return; // Cerrar sesión
            }
        } while (true);
    }

    /**
     * Flujo completo para registrar un nuevo usuario en la base.
     */
    public static void registrarUsuario() {
        // 1. Elegir rol
        String[] roles = {"PACIENTE", "MEDICO", "ADMIN"};
        int rolIdx = JOptionPane.showOptionDialog(null, "Que tipo de usuario desea registrar?",
                "Registrar Usuario", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
        if (rolIdx == JOptionPane.CLOSED_OPTION) return;
        String rol = roles[rolIdx];

        // 2. Pedir datos comunes
        String dni = JOptionPane.showInputDialog("DNI:");
        if (dni == null || dni.isEmpty()) return;

        String contrasenia = JOptionPane.showInputDialog("Contrasenia:");
        if (contrasenia == null || contrasenia.isEmpty()) return;

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.isEmpty()) return;

        String apellido = JOptionPane.showInputDialog("Apellido:");
        if (apellido == null || apellido.isEmpty()) return;

        String email = JOptionPane.showInputDialog("Email:");
        if (email == null || email.isEmpty()) return;

        String telefono = JOptionPane.showInputDialog("Telefono:");
        if (telefono == null) return; // Telefono puede estar vacio

        // 3. Insertar usuario base y obtener el ID
        UsuarioController uc = new UsuarioController();
        int idUsuario = uc.registrarUsuario(dni, contrasenia, nombre, apellido, email, telefono, rol);

        if (idUsuario == -1) {
            JOptionPane.showMessageDialog(null, "Error al registrar el usuario.\nVerifica que el DNI no este repetido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Segun el rol, pedir datos extra
        boolean ok = true;
        if (rol.equals("PACIENTE")) {
            ok = registrarDatosPaciente(idUsuario);
        } else if (rol.equals("MEDICO")) {
            ok = registrarDatosMedico(idUsuario);
        }
        // Si es ADMIN, no necesita datos extra.

        if (ok) {
            JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.\nID: " + idUsuario);
        } else {
            JOptionPane.showMessageDialog(null, "El usuario base se creo pero hubo un error con los datos especificos.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static boolean registrarDatosPaciente(int idUsuario) {
        // Fecha de nacimiento
        String fechaStr = JOptionPane.showInputDialog("Fecha de nacimiento (YYYY-MM-DD):");
        if (fechaStr == null || fechaStr.isEmpty()) return false;

        LocalDate fechaNac;
        try {
            fechaNac = LocalDate.parse(fechaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha invalido. Use YYYY-MM-DD.");
            return false;
        }

        // Domicilio
        String domicilio = JOptionPane.showInputDialog("Domicilio:");
        if (domicilio == null || domicilio.isEmpty()) return false;

        // Obra social (mostrar lista)
        ObraSocialController osc = new ObraSocialController();
        List<String> obras = osc.listarObrasSociales();
        if (obras.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay obras sociales cargadas en el sistema.");
            return false;
        }

        String[] obrasArr = obras.toArray(new String[0]);
        String elegida = (String) JOptionPane.showInputDialog(null, "Seleccione obra social:",
                "Obra Social", JOptionPane.QUESTION_MESSAGE, null, obrasArr, obrasArr[0]);
        if (elegida == null) return false;

        int idObraSocial = Integer.parseInt(elegida.split(" - ")[0]);

        // Insertar paciente y crear historia clinica
        UsuarioController uc = new UsuarioController();
        boolean pacienteOk = uc.registrarPaciente(idUsuario, fechaNac, domicilio, idObraSocial);
        boolean hcOk = uc.crearHistoriaClinica(idUsuario);

        return pacienteOk && hcOk;
    }

    private static boolean registrarDatosMedico(int idUsuario) {
        // Matricula
        String matricula = JOptionPane.showInputDialog("Matricula:");
        if (matricula == null || matricula.isEmpty()) return false;

        // Especialidad
        EspecialidadController ec = new EspecialidadController();
        List<String> especialidades = ec.listarEspecialidades();
        if (especialidades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay especialidades cargadas en el sistema.");
            return false;
        }

        String[] espArr = especialidades.toArray(new String[0]);
        String elegida = (String) JOptionPane.showInputDialog(null, "Seleccione especialidad:",
                "Especialidad", JOptionPane.QUESTION_MESSAGE, null, espArr, espArr[0]);
        if (elegida == null) return false;

        int idEspecialidad = Integer.parseInt(elegida.split(" - ")[0]);

        // Insertar medico
        UsuarioController uc = new UsuarioController();
        return uc.registrarMedico(idUsuario, matricula, idEspecialidad);
    }

    /**
     * Menú para Médico y Paciente
     */
    public static void menuInterno(String rol, String[] opciones) {
        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(null, "Panel de " + rol,
                    "HealthHubCPS", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

            if (seleccion != JOptionPane.CLOSED_OPTION && seleccion != opciones.length - 1) {
                JOptionPane.showMessageDialog(null, "Has seleccionado: " + opciones[seleccion]);
            }
        } while (seleccion != opciones.length - 1 && seleccion != JOptionPane.CLOSED_OPTION);
    }
}