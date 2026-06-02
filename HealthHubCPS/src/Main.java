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
                            menuPaciente(opPaciente, user);
                            break;
                    }
                }
            }
        } while (opcion != 1 && opcion != JOptionPane.CLOSED_OPTION);
    }

    /**
     * Menú funcional del Paciente
     */
    public static void menuPaciente(String[] opciones, Usuario pacienteLogueado) {
        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(null,
                    "Bienvenido " + pacienteLogueado.getNombre() + " " + pacienteLogueado.getApellido(),
                    "Panel de Paciente - HealthHubCPS",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION) break;

            switch (seleccion) {
                case 0: // Completar Datos
                    completarDatosPaciente(pacienteLogueado.getId());
                    break;
                case 1: // Actualizar Datos Personales
                    actualizarDatosPersonales(pacienteLogueado);
                    break;
                case 2: // Solicitar Turno
                    solicitarTurno(pacienteLogueado.getId());
                    break;
                case 3: // Ver Mis Turnos
                    verMisTurnos(pacienteLogueado.getId());
                    break;
                case 4: // Cancelar Turno
                    cancelarTurno(pacienteLogueado.getId());
                    break;
                case 5: // Ver Resultados
                    verResultados(pacienteLogueado.getId());
                    break;
                case 6: // Cerrar Sesion
                    return;
            }
        } while (true);
    }

    /**
     * Completa los datos del paciente (fecha nacimiento, domicilio, obra social)
     */
    private static void completarDatosPaciente(int idPaciente) {
        UsuarioController uc = new UsuarioController();

        // Verificar si el paciente ya tiene datos completos
        if (uc.pacienteTieneDatos(idPaciente)) {
            JOptionPane.showMessageDialog(null, "Ya tienes todos tus datos completados.");
            return;
        }

        String fechaStr = JOptionPane.showInputDialog(null, "Fecha de nacimiento (YYYY-MM-DD):");
        if (fechaStr == null || fechaStr.isEmpty()) return;

        LocalDate fechaNac;
        try {
            fechaNac = LocalDate.parse(fechaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha inválido. Use YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String domicilio = JOptionPane.showInputDialog(null, "Domicilio:");
        if (domicilio == null || domicilio.isEmpty()) return;

        ObraSocialController osc = new ObraSocialController();
        List<String> obras = osc.listarObrasSociales();
        if (obras.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay obras sociales cargadas en el sistema.");
            return;
        }

        String[] obrasArr = obras.toArray(new String[0]);
        String elegida = (String) JOptionPane.showInputDialog(null, "Seleccione su obra social:",
                "Obra Social", JOptionPane.QUESTION_MESSAGE, null, obrasArr, obrasArr[0]);
        if (elegida == null) return;

        int idObraSocial = Integer.parseInt(elegida.split(" - ")[0]);

        boolean ok = uc.completarDatosPaciente(idPaciente, fechaNac, domicilio, idObraSocial);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Datos completados exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al completar los datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza los datos personales del paciente
     */
    private static void actualizarDatosPersonales(Usuario paciente) {
        String[] opciones = {"Nombre", "Apellido", "Email", "Teléfono", "Volver"};
        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(null,
                    "¿Qué dato desea actualizar?",
                    "Actualizar Datos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION || seleccion == 4) break;

            switch (seleccion) {
                case 0: // Nombre
                    String nuevoNombre = JOptionPane.showInputDialog(null, "Nuevo nombre:", paciente.getNombre());
                    if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                        if (actualizarDato(paciente.getId(), "nombre", nuevoNombre)) {
                            paciente.setNombre(nuevoNombre);
                            JOptionPane.showMessageDialog(null, "Nombre actualizado.");
                        }
                    }
                    break;
                case 1: // Apellido
                    String nuevoApellido = JOptionPane.showInputDialog(null, "Nuevo apellido:", paciente.getApellido());
                    if (nuevoApellido != null && !nuevoApellido.isEmpty()) {
                        if (actualizarDato(paciente.getId(), "apellido", nuevoApellido)) {
                            paciente.setApellido(nuevoApellido);
                            JOptionPane.showMessageDialog(null, "Apellido actualizado.");
                        }
                    }
                    break;
                case 2: // Email
                    String nuevoEmail = JOptionPane.showInputDialog(null, "Nuevo email:", paciente.getEmail());
                    if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
                        if (actualizarDato(paciente.getId(), "email", nuevoEmail)) {
                            paciente.setEmail(nuevoEmail);
                            JOptionPane.showMessageDialog(null, "Email actualizado.");
                        }
                    }
                    break;
                case 3: // Teléfono
                    String nuevoTel = JOptionPane.showInputDialog(null, "Nuevo teléfono:", paciente.getTelefono());
                    if (nuevoTel != null) {
                        if (actualizarDato(paciente.getId(), "telefono", nuevoTel)) {
                            paciente.setTelefono(nuevoTel);
                            JOptionPane.showMessageDialog(null, "Teléfono actualizado.");
                        }
                    }
                    break;
            }
        } while (true);
    }

    /**
     * Actualiza un dato del usuario
     */
    private static boolean actualizarDato(int idUsuario, String campo, String valor) {
        UsuarioController uc = new UsuarioController();
        return uc.actualizarUsuario(idUsuario, campo, valor);
    }

    /**
     * Solicita un nuevo turno para el paciente
     */
    private static void solicitarTurno(int idPaciente) {
        TurnoController tc = new TurnoController();

        // Seleccionar especialidad
        List<String> especialidades = tc.listarEspecialidades();
        if (especialidades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay especialidades disponibles.");
            return;
        }

        String[] espArr = especialidades.toArray(new String[0]);
        String espElegida = (String) JOptionPane.showInputDialog(null,
                "Seleccione especialidad:",
                "Solicitar Turno", JOptionPane.QUESTION_MESSAGE, null, espArr, espArr[0]);
        if (espElegida == null) return;

        int idEspecialidad = Integer.parseInt(espElegida.split(" - ")[0]);

        // Seleccionar médico de esa especialidad
        List<String> medicos = tc.listarMedicosPorEspecialidad(idEspecialidad);
        if (medicos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay médicos disponibles en esta especialidad.");
            return;
        }

        String[] medArr = medicos.toArray(new String[0]);
        String medElegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione médico:",
                "Solicitar Turno", JOptionPane.QUESTION_MESSAGE, null, medArr, medArr[0]);
        if (medElegido == null) return;

        int idMedico = Integer.parseInt(medElegido.split(" - ")[0]);

        // Seleccionar tipo de estudio
        List<String> estudios = tc.listarTiposEstudio();
        if (estudios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tipos de estudio disponibles.");
            return;
        }

        String[] estArr = estudios.toArray(new String[0]);
        String estElegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione tipo de estudio:",
                "Solicitar Turno", JOptionPane.QUESTION_MESSAGE, null, estArr, estArr[0]);
        if (estElegido == null) return;

        int idTipoEstudio = Integer.parseInt(estElegido.split(" - ")[0]);

        // Seleccionar fecha
        String fechaStr = JOptionPane.showInputDialog(null, "Fecha del turno (YYYY-MM-DD):");
        if (fechaStr == null || fechaStr.isEmpty()) return;

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha inválido.");
            return;
        }

        // Seleccionar hora
        String hora = JOptionPane.showInputDialog(null, "Hora (HH:mm):");
        if (hora == null || hora.isEmpty()) return;

        // Seleccionar consultorio
        List<String> consultorios = tc.listarConsultorios();
        if (consultorios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay consultorios disponibles.");
            return;
        }

        String[] consArr = consultorios.toArray(new String[0]);
        String consElegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione consultorio:",
                "Solicitar Turno", JOptionPane.QUESTION_MESSAGE, null, consArr, consArr[0]);
        if (consElegido == null) return;

        int idConsultorio = Integer.parseInt(consElegido.split(" - ")[0]);

        // Calcular monto final con cobertura
        double montoFinal = tc.calcularMontoTurno(idTipoEstudio, idPaciente);

        // Crear el turno
        int idTurno = tc.crearTurno(fecha, hora, idPaciente, idMedico, idConsultorio, idTipoEstudio, montoFinal);
        if (idTurno > 0) {
            JOptionPane.showMessageDialog(null,
                    "Turno solicitado exitosamente.\nID: " + idTurno + "\nMonto a pagar: $" + montoFinal);
        } else {
            JOptionPane.showMessageDialog(null, "Error al solicitar el turno.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra los turnos del paciente
     */
    private static void verMisTurnos(int idPaciente) {
        TurnoController tc = new TurnoController();
        List<String> turnos = tc.listarTurnosPaciente(idPaciente);

        if (turnos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tienes turnos agendados.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("TUS TURNOS:\n\n");
        for (String turno : turnos) {
            sb.append(turno).append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(),
                "Mis Turnos", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cancela un turno del paciente
     */
    private static void cancelarTurno(int idPaciente) {
        TurnoController tc = new TurnoController();
        List<String> turnos = tc.listarTurnosActivos(idPaciente);

        if (turnos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tienes turnos para cancelar.");
            return;
        }

        String[] turnArr = turnos.toArray(new String[0]);
        String turnoElegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione turno a cancelar:",
                "Cancelar Turno", JOptionPane.QUESTION_MESSAGE, null, turnArr, turnArr[0]);
        if (turnoElegido == null) return;

        int idTurno = Integer.parseInt(turnoElegido.split(" - ")[0]);

        String motivo = JOptionPane.showInputDialog(null, "Motivo de cancelación:");
        if (motivo == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de cancelar este turno?",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (tc.cancelarTurno(idTurno, motivo)) {
                JOptionPane.showMessageDialog(null, "Turno cancelado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al cancelar el turno.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Muestra los resultados de estudios autorizados del paciente
     */
    private static void verResultados(int idPaciente) {
        TurnoController tc = new TurnoController();
        List<String> resultados = tc.listarResultadosPaciente(idPaciente);

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tienes resultados disponibles aún.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("TUS RESULTADOS DE ESTUDIOS:\n\n");
        for (String resultado : resultados) {
            sb.append(resultado).append("\n\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(),
                "Resultados de Estudios", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menu del admin con las funciones implementadas.
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
                    consultarConsultorioDeMedico();
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
        String[] roles = {"PACIENTE", "MEDICO", "ADMIN"};
        int rolIdx = JOptionPane.showOptionDialog(null, "Que tipo de usuario desea registrar?",
                "Registrar Usuario", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
        if (rolIdx == JOptionPane.CLOSED_OPTION) return;
        String rol = roles[rolIdx];

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

        UsuarioController uc = new UsuarioController();
        int idUsuario = uc.registrarUsuario(dni, contrasenia, nombre, apellido, email, telefono, rol);

        if (idUsuario == -1) {
            JOptionPane.showMessageDialog(null, "Error al registrar el usuario.\nVerifica que el DNI no este repetido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = true;
        if (rol.equals("PACIENTE")) {
            ok = registrarDatosPaciente(idUsuario);
        } else if (rol.equals("MEDICO")) {
            ok = registrarDatosMedico(idUsuario);
        }

        if (ok) {
            JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.\nID: " + idUsuario);
        } else {
            JOptionPane.showMessageDialog(null, "El usuario base se creo pero hubo un error con los datos especificos.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static boolean registrarDatosPaciente(int idUsuario) {
        String fechaStr = JOptionPane.showInputDialog("Fecha de nacimiento (YYYY-MM-DD):");
        if (fechaStr == null || fechaStr.isEmpty()) return false;

        LocalDate fechaNac;
        try {
            fechaNac = LocalDate.parse(fechaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha invalido. Use YYYY-MM-DD.");
            return false;
        }

        String domicilio = JOptionPane.showInputDialog("Domicilio:");
        if (domicilio == null || domicilio.isEmpty()) return false;

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

        UsuarioController uc = new UsuarioController();
        boolean pacienteOk = uc.registrarPaciente(idUsuario, fechaNac, domicilio, idObraSocial);
        boolean hcOk = uc.crearHistoriaClinica(idUsuario);

        return pacienteOk && hcOk;
    }

    private static boolean registrarDatosMedico(int idUsuario) {
        String matricula = JOptionPane.showInputDialog("Matricula:");
        if (matricula == null || matricula.isEmpty()) return false;

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

        UsuarioController uc = new UsuarioController();
        return uc.registrarMedico(idUsuario, matricula, idEspecialidad);
    }

    public static void gestionarEstadoUsuario(Usuario adminLogueado) {
        UsuarioController uc = new UsuarioController();
        List<String> usuarios = uc.listarTodosLosUsuarios();

        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios en el sistema.");
            return;
        }

        String[] userArr = usuarios.toArray(new String[0]);
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione usuario a modificar:",
                "Gestionar Usuario", JOptionPane.QUESTION_MESSAGE,
                null, userArr, userArr[0]);
        if (elegido == null) return;

        int idUsuario = Integer.parseInt(elegido.split(" - ")[0]);
        boolean estaActivo = elegido.contains("[ACTIVO]");

        String[] opciones = {estaActivo ? "Desactivar" : "Activar", "Volver"};
        int idx = JOptionPane.showOptionDialog(null,
                "¿Que desea hacer?",
                "Cambiar Estado",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (idx == 0) {
            boolean ok = uc.cambiarEstadoUsuario(idUsuario, !estaActivo);
            if (ok) {
                JOptionPane.showMessageDialog(null, "Estado actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al cambiar el estado.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void consultarConsultorioDeMedico() {
        UsuarioController uc = new UsuarioController();
        List<String> medicos = uc.listarMedicosActivos();

        if (medicos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay medicos activos en el sistema.");
            return;
        }

        String[] medArr = medicos.toArray(new String[0]);
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione el medico:",
                "Consultorio Asignado", JOptionPane.QUESTION_MESSAGE,
                null, medArr, medArr[0]);
        if (elegido == null) return;

        int idMedico = Integer.parseInt(elegido.split(" - ")[0]);

        ConsultorioController cc = new ConsultorioController();
        List<String> consultorios = cc.consultoriosDeMedico(idMedico, 30);

        StringBuilder msg = new StringBuilder();
        msg.append("Medico: ").append(elegido).append("\n\n");

        if (consultorios.isEmpty()) {
            msg.append("Este medico aun no tiene consultorios asignados\n");
            msg.append("(no tiene turnos agendados en los proximos 30 dias).\n\n");
            msg.append("El consultorio se asigna al crear cada turno.");
        } else {
            msg.append("Consultorios donde atiende en los proximos 30 dias:\n\n");
            for (String c : consultorios) {
                msg.append("  - ").append(c).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null, msg.toString(),
                "Consultorios del Medico", JOptionPane.INFORMATION_MESSAGE);
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
     * Menu para Medico.
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
