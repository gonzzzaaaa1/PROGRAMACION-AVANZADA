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
                "Registrar Usuario", "Gestionar Estado de Usuario", "Asignar Consultorio a Medico",
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
                            menuAdmin(opAdmin, user);
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
     * Menu del admin con las funciones implementadas.
     * Recibe el usuario logueado para evitar que se de de baja a si mismo.
     */
    public static void menuAdmin(String[] opciones, Usuario adminLogueado) {
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
                    gestionarEstadoUsuario(adminLogueado);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Funcion pendiente: Asignar Consultorio");
                    break;
                case 3:
                    configurarTiemposDeTurno();
                    break;
                case 4:
                    configurarTarifasYCoberturas();
                    break;
                case 5:
                    return; // Cerrar sesion
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
        if (telefono == null) return;

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
     * Permite alternar el estado de un usuario (activo <-> inactivo).
     * No permite que el admin logueado se modifique a si mismo.
     */
    public static void gestionarEstadoUsuario(Usuario adminLogueado) {
        UsuarioController uc = new UsuarioController();
        List<String> usuarios = uc.listarTodosLosUsuarios();

        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios en el sistema.");
            return;
        }

        String[] usuariosArr = usuarios.toArray(new String[0]);
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione el usuario:",
                "Gestionar Estado de Usuario",
                JOptionPane.QUESTION_MESSAGE, null, usuariosArr, usuariosArr[0]);

        if (elegido == null) return;

        int idUsuario = Integer.parseInt(elegido.split(" - ")[0]);

        // Validacion: no puede modificar su propio estado
        if (idUsuario == adminLogueado.getId()) {
            JOptionPane.showMessageDialog(null,
                    "No podes modificar tu propio estado mientras estas logueado.",
                    "Accion no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Detectar estado actual desde el string que elegimos
        boolean estaActivo = elegido.contains("[ACTIVO]");
        boolean nuevoEstado = !estaActivo;
        String accion = nuevoEstado ? "DAR DE ALTA" : "DAR DE BAJA";

        int confirm = JOptionPane.showConfirmDialog(null,
                "Esta seguro que desea " + accion + " a:\n" + elegido + "?",
                "Confirmar accion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = uc.cambiarEstadoUsuario(idUsuario, nuevoEstado);

        if (ok) {
            String msg = nuevoEstado ? "Usuario dado de alta exitosamente." : "Usuario dado de baja exitosamente.";
            JOptionPane.showMessageDialog(null, msg);
        } else {
            JOptionPane.showMessageDialog(null, "Error al modificar el estado del usuario.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Pregunta si quiere modificar tarifas o coberturas, y dispara la accion.
     */
    public static void configurarTarifasYCoberturas() {
        String[] opciones = {"Tarifa de un estudio", "Cobertura de obra social"};
        int idx = JOptionPane.showOptionDialog(null,
                "Que desea modificar?",
                "Configurar Tarifas y Coberturas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (idx == JOptionPane.CLOSED_OPTION) return;

        if (idx == 0) {
            configurarTarifa();
        } else {
            configurarCobertura();
        }
    }

    private static void configurarTarifa() {
        TipoEstudioController tec = new TipoEstudioController();
        List<String> estudios = tec.listarTiposEstudio();

        if (estudios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tipos de estudio en el sistema.");
            return;
        }

        String[] estArr = estudios.toArray(new String[0]);
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione el estudio:",
                "Modificar Tarifa", JOptionPane.QUESTION_MESSAGE,
                null, estArr, estArr[0]);
        if (elegido == null) return;

        int idEstudio = Integer.parseInt(elegido.split(" - ")[0]);

        String nuevaStr = JOptionPane.showInputDialog("Ingrese la nueva tarifa (en pesos):");
        if (nuevaStr == null || nuevaStr.isEmpty()) return;

        double nuevaTarifa;
        try {
            nuevaTarifa = Double.parseDouble(nuevaStr);
            if (nuevaTarifa < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La tarifa debe ser un numero positivo.");
            return;
        }

        boolean ok = tec.actualizarTarifa(idEstudio, nuevaTarifa);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Tarifa actualizada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar la tarifa.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void configurarCobertura() {
        CoberturaController cc = new CoberturaController();
        List<String> coberturas = cc.listarCoberturas();

        if (coberturas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay coberturas en el sistema.");
            return;
        }

        String[] covArr = coberturas.toArray(new String[0]);
        String elegida = (String) JOptionPane.showInputDialog(null,
                "Seleccione la cobertura a modificar:",
                "Modificar Cobertura", JOptionPane.QUESTION_MESSAGE,
                null, covArr, covArr[0]);
        if (elegida == null) return;

        // Formato: "idOS-idTE -"
        String[] ids = elegida.split(" - ")[0].split("-");
        int idObraSocial = Integer.parseInt(ids[0]);
        int idTipoEstudio = Integer.parseInt(ids[1]);

        String nuevoStr = JOptionPane.showInputDialog("Ingrese el nuevo porcentaje (0 a 99.99):");
        if (nuevoStr == null || nuevoStr.isEmpty()) return;

        double nuevoPorc;
        try {
            nuevoPorc = Double.parseDouble(nuevoStr);
            if (nuevoPorc < 0 || nuevoPorc > 99.99) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El porcentaje debe estar entre 0 y 99.99.");
            return;
        }

        boolean ok = cc.actualizarCobertura(idObraSocial, idTipoEstudio, nuevoPorc);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Cobertura actualizada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar la cobertura.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Permite cambiar el tiempo en minutos de un tipo de estudio.
     */
    public static void configurarTiemposDeTurno() {
        TipoEstudioController tec = new TipoEstudioController();
        List<String> estudios = tec.listarTiposEstudioConTiempo();

        if (estudios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tipos de estudio en el sistema.");
            return;
        }

        String[] estArr = estudios.toArray(new String[0]);
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione el estudio:",
                "Configurar Tiempo de Turno", JOptionPane.QUESTION_MESSAGE,
                null, estArr, estArr[0]);
        if (elegido == null) return;

        int idEstudio = Integer.parseInt(elegido.split(" - ")[0]);

        String nuevoStr = JOptionPane.showInputDialog("Ingrese la nueva duracion en minutos:");
        if (nuevoStr == null || nuevoStr.isEmpty()) return;

        int nuevoTiempo;
        try {
            nuevoTiempo = Integer.parseInt(nuevoStr);
            if (nuevoTiempo <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El tiempo debe ser un numero entero positivo.");
            return;
        }

        boolean ok = tec.actualizarTiempo(idEstudio, nuevoTiempo);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Tiempo actualizado exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar el tiempo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menu para Medico y Paciente
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