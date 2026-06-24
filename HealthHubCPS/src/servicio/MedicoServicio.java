package servicio;

import dao.ArchivoAdjuntoDAO;
import dao.HistoriaClinicaDAO;
import dao.MedicoDAO;
import dao.ResultadoDAO;
import dao.TurnoDAO;
import modelo.ArchivoAdjunto;
import modelo.FormatoArchivo;
import modelo.HistoriaClinica;
import modelo.Resultado;
import modelo.TipoArchivo;
import modelo.Turno;
import modelo.Usuario;

import java.util.List;


public class MedicoServicio {

    private final TurnoDAO turnoDAO = new TurnoDAO();
    private final ResultadoDAO resultadoDAO = new ResultadoDAO();
    private final HistoriaClinicaDAO historiaDAO = new HistoriaClinicaDAO();
    private final ArchivoAdjuntoDAO archivoDAO = new ArchivoAdjuntoDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    public List<Usuario> listarPacientes(int idMedico) {
        return medicoDAO.listarPacientesDelMedico(idMedico);
    }

    public HistoriaClinica verHistoria(int idPaciente) {
        return historiaDAO.buscarPorPaciente(idPaciente);
    }

    public List<ArchivoAdjunto> listarArchivos(int idPaciente) {
        return archivoDAO.listarPorPaciente(idPaciente);
    }

    public Respuesta adjuntarArchivo(int idTurno, int idMedico, TipoArchivo tipo,
                                     FormatoArchivo formato, String url) {
        int idHistoria = historiaDAO.obtenerIdHistoriaDeTurno(idTurno);
        if (idHistoria == -1) {
            return Respuesta.error("El paciente de ese turno no tiene historia clinica.");
        }
        if (url == null || url.trim().isEmpty()) {
            return Respuesta.error("Tenes que indicar la URL o ruta del archivo.");
        }
        ArchivoAdjunto a = new ArchivoAdjunto();
        a.setIdHistoria(idHistoria);
        a.setIdMedicoCarga(idMedico);
        a.setTipo(tipo);
        a.setFormato(formato);
        a.setUrl(url);
        if (archivoDAO.insertar(a)) {
            return Respuesta.ok("Archivo adjuntado a la historia clinica.");
        }
        return Respuesta.error("No se pudo adjuntar el archivo.");
    }

    public Respuesta subirResultado(int idTurno, String descripcion, boolean autorizarAhora, int idMedico) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return Respuesta.error("La descripcion del resultado no puede estar vacia.");
        }
        Resultado r = new Resultado();
        r.setIdTurno(idTurno);
        r.setDescripcion(descripcion);
        r.setAutorizado(autorizarAhora);
        r.setIdMedicoAutoriza(idMedico);
        if (resultadoDAO.insertar(r)) {
            return Respuesta.ok(autorizarAhora
                    ? "Resultado cargado y autorizado."
                    : "Resultado cargado. Queda pendiente de autorizacion.");
        }
        return Respuesta.error("No se pudo cargar el resultado (puede que ese turno ya tenga uno).");
    }

    public Respuesta autorizarResultado(int idResultado, int idMedico) {
        if (resultadoDAO.autorizar(idResultado, idMedico)) {
            return Respuesta.ok("Resultado autorizado.");
        }
        return Respuesta.error("No se pudo autorizar el resultado.");
    }

    public List<Turno> listarTurnosSinResultado(int idMedico) {
        return turnoDAO.listarSinResultadoMedico(idMedico);
    }

    public List<Resultado> listarResultadosPendientes(int idMedico) {
        return resultadoDAO.listarPendientesMedico(idMedico);
    }
}