package servicio;

import dao.CoberturaDAO;
import dao.ConsultorioDAO;
import dao.EspecialidadDAO;
import dao.MedicoDAO;
import dao.ObraSocialDAO;
import dao.TipoEstudioDAO;
import modelo.Cobertura;
import modelo.Consultorio;
import modelo.Especialidad;
import modelo.Medico;
import modelo.ObraSocial;
import modelo.TipoEstudio;

import java.util.List;

/**
 * Provee los datos de catalogo (para los combos de la interfaz) y centraliza
 * las configuraciones del administrador (tarifas, tiempos y coberturas).
 */
public class CatalogoServicio {

    private final ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();
    private final TipoEstudioDAO tipoEstudioDAO = new TipoEstudioDAO();
    private final ConsultorioDAO consultorioDAO = new ConsultorioDAO();
    private final CoberturaDAO coberturaDAO = new CoberturaDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    public List<ObraSocial> listarObrasSociales() {
        return obraSocialDAO.listarActivas();
    }

    public List<Especialidad> listarEspecialidades() {
        return especialidadDAO.listarTodos();
    }

    public List<TipoEstudio> listarTiposEstudio() {
        return tipoEstudioDAO.listarTodos();
    }

    public List<Consultorio> listarConsultorios() {
        return consultorioDAO.listarTodos();
    }

    public List<Medico> listarMedicosActivos() {
        return medicoDAO.listarActivos();
    }

    public List<Cobertura> listarCoberturas() {
        return coberturaDAO.listarTodas();
    }

    /** Consultorios donde un medico tiene turnos en los proximos N dias. */
    public List<Consultorio> consultoriosDeMedico(int idMedico, int dias) {
        return consultorioDAO.consultoriosDeMedico(idMedico, dias);
    }

    public Respuesta actualizarTarifa(int idTipoEstudio, double tarifa) {
        if (tarifa < 0) {
            return Respuesta.error("La tarifa no puede ser negativa.");
        }
        if (tipoEstudioDAO.actualizarTarifa(idTipoEstudio, tarifa)) {
            return Respuesta.ok("Tarifa actualizada.");
        }
        return Respuesta.error("No se pudo actualizar la tarifa.");
    }

    public Respuesta actualizarTiempo(int idTipoEstudio, int minutos) {
        if (minutos <= 0) {
            return Respuesta.error("El tiempo debe ser mayor a 0 minutos.");
        }
        if (tipoEstudioDAO.actualizarTiempo(idTipoEstudio, minutos)) {
            return Respuesta.ok("Tiempo actualizado.");
        }
        return Respuesta.error("No se pudo actualizar el tiempo.");
    }

    public Respuesta actualizarCobertura(int idObraSocial, int idTipoEstudio, double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            return Respuesta.error("El porcentaje debe estar entre 0 y 100.");
        }
        if (coberturaDAO.actualizarPorcentaje(idObraSocial, idTipoEstudio, porcentaje)) {
            return Respuesta.ok("Cobertura actualizada.");
        }
        return Respuesta.error("No se pudo actualizar la cobertura (verifica que exista esa combinacion).");
    }
}
