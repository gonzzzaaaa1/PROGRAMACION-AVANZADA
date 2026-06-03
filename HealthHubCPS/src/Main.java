import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

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

        String fechaStr = pedirDatoValidado("Fecha de nacimiento (YYYY-MM-DD):",
                Validaciones::validarFechaNacimiento);
        if (fechaStr == null) return;
        LocalDate fechaNac = LocalDate.parse(fechaStr);

        String domicilio = pedirDatoValidado("Domicilio:", Validaciones::validarDomicilio);
        if (domicilio == null) return;

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
                    String nuevoNombre = pedirDatoValidadoConValor("Nuevo nombre:",
                            paciente.getNombre(),
                            v -> Validaciones.validarNombre(v, "El nombre"));
                    if (nuevoNombre != null) {
                        if (actualizarDato(paciente.getId(), "nombre", nuevoNombre)) {
                            paciente.setNombre(nuevoNombre);
                            JOptionPane.showMessageDialog(null, "Nombre actualizado.");
                        }
                    }
                    break;
                case 1: // Apellido
                    String nuevoApellido = pedirDatoValidadoConValor("Nuevo apellido:",
                            paciente.getApellido(),
                            v -> Validaciones.validarNombre(v, "El apellido"));
                    if (nuevoApellido != null) {
                        if (actualizarDato(paciente.getId(), "apellido", nuevoApellido)) {
                            paciente.setApellido(nuevoApellido);
                            JOptionPane.showMessageDialog(null, "Apellido actualizado.");
                        }
                    }
                    break;
                case 2: // Email
                    String nuevoEmail = pedirDatoValidadoConValor("Nuevo email:",
                            paciente.getEmail(),
                            Validaciones::validarEmail);
                    if (nuevoEmail != null) {
                        if (actualizarDato(paciente.getId(), "email", nuevoEmail)) {
                            paciente.setEmail(nuevoEmail);
                            JOptionPane.showMessageDialog(null, "Email actualizado.");
                        }
                    }
                    break;
                case 3: // Teléfono
                    String nuevoTel = pedirDatoValidadoConValor("Nuevo teléfono:",
                            paciente.getTelefono(),
                            Validaciones::validarTelefono);
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
     * Variante de pedirDatoValidado que muestra un valor inicial (el actual).
     * Se usa al actualizar datos, donde queremos mostrar el valor previo.
     */
    private static String pedirDatoValidadoConValor(String mensaje, String valorInicial,
                                                    Function<String, String> validador) {
        while (true) {
            String valor = JOptionPane.showInputDialog(null, mensaje, valorInicial);
            if (valor == null) return null; // canceló
            String error = validador.apply(valor);
            if (error == null) return valor.trim();
            JOptionPane.showMessageDialog(null, error, "Dato invalido", JOptionPane.ERROR_MESSAGE);
        }
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
        String fechaStr = pedirDatoValidado("Fecha del turno (YYYY-MM-DD):",
                Validaciones::validarFechaTurno);
        if (fechaStr == null) return;
        LocalDate fecha = LocalDate.parse(fechaStr);

        // Seleccionar hora
        String hora = pedirDatoValidado("Hora (HH:mm):", Validaciones::validarHoraTurno);
        if (hora == null) return;

        // Si la fecha es hoy, la hora debe ser futura
        String errorHora = Validaciones.validarHoraFutura(fecha, hora);
        if (errorHora != null) {
            JOptionPane.showMessageDialog(null, errorHora, "Hora invalida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Asignar consultorio automaticamente
        String[] consAsignado = tc.asignarConsultorioAutomatico(fecha, hora);
        if (consAsignado == null) {
            JOptionPane.showMessageDialog(null,
                    "No hay consultorios disponibles para esa fecha y horario.",
                    "Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idConsultorio = Integer.parseInt(consAsignado[0]);
        String ubicacionConsultorio = consAsignado[1];

        // Solo se le informa la ubicacion al paciente
        JOptionPane.showMessageDialog(null,
                "El consultorio fue asignado automaticamente.\nUbicacion: " + ubicacionConsultorio,
                "Consultorio asignado", JOptionPane.INFORMATION_MESSAGE);

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

        String motivo = pedirDatoValidado("Motivo de cancelación:",
                Validaciones::validarMotivoCancelacion);
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
     * Pide un dato al usuario y lo valida con la funcion dada.
     * Si es invalido, muestra el error y vuelve a preguntar.
     * Devuelve el valor (trim) si es valido, o null si el usuario cancelo.
     */
    private static String pedirDatoValidado(String mensaje, Function<String, String> validador) {
        while (true) {
            String valor = JOptionPane.showInputDialog(mensaje);
            if (valor == null) return null; // canceló
            String error = validador.apply(valor);
            if (error == null) return valor.trim();
            JOptionPane.showMessageDialog(null, error, "Dato invalido", JOptionPane.ERROR_MESSAGE);
        }
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

        String dni = pedirDatoValidado("DNI:", Validaciones::validarDni);
        if (dni == null) return;

        String contrasenia = pedirDatoValidado("Contrasenia:", Validaciones::validarContrasenia);
        if (contrasenia == null) return;

        String nombre = pedirDatoValidado("Nombre:", v -> Validaciones.validarNombre(v, "El nombre"));
        if (nombre == null) return;

        String apellido = pedirDatoValidado("Apellido:", v -> Validaciones.validarNombre(v, "El apellido"));
        if (apellido == null) return;

        String email = pedirDatoValidado("Email:", Validaciones::validarEmail);
        if (email == null) return;

        String telefono = pedirDatoValidado("Telefono (opcional):", Validaciones::validarTelefono);
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
        String fechaStr = pedirDatoValidado("Fecha de nacimiento (YYYY-MM-DD):",
                Validaciones::validarFechaNacimiento);
        if (fechaStr == null) return false;
        LocalDate fechaNac = LocalDate.parse(fechaStr);

        String domicilio = pedirDatoValidado("Domicilio:", Validaciones::validarDomicilio);
        if (domicilio == null) return false;

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
        String matricula = pedirDatoValidado("Matricula:", Validaciones::validarMatricula);
        if (matricula == null) return false;

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

        String nuevaStr = pedirDatoValidado("Ingrese la nueva tarifa (en pesos):",
                Validaciones::validarTarifa);
        if (nuevaStr == null) return;
        double nuevaTarifa = Double.parseDouble(nuevaStr);

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

        String nuevoStr = pedirDatoValidado("Ingrese el nuevo porcentaje (0 a 99.99):",
                Validaciones::validarPorcentajeCobertura);
        if (nuevoStr == null) return;
        double nuevoPorc = Double.parseDouble(nuevoStr);

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

        String nuevoStr = pedirDatoValidado("Ingrese la nueva duracion en minutos:",
                Validaciones::validarDuracionMinutos);
        if (nuevoStr == null) return;
        int nuevoTiempo = Integer.parseInt(nuevoStr);

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