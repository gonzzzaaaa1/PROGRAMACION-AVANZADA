package servicio;

import dao.HistoriaClinicaDAO;
import dao.PacienteDAO;
import dao.ResultadoDAO;
import dao.UsuarioDAO;
import modelo.HistoriaClinica;
import modelo.Paciente;
import modelo.Resultado;
import modelo.Usuario;

import java.time.LocalDate;
import java.util.List;


public class PacienteServicio {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final HistoriaClinicaDAO historiaDAO = new HistoriaClinicaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ResultadoDAO resultadoDAO = new ResultadoDAO();


    public boolean tieneDatos(int idUsuario) {
        return pacienteDAO.tieneDatos(idUsuario);
    }


    public Respuesta completarDatos(int idUsuario, LocalDate fechaNac, String domicilio, int idObraSocial) {
        if (pacienteDAO.tieneDatos(idUsuario)) {
            return Respuesta.error("Ya completaste tus datos. Usa 'Actualizar datos' para modificarlos.");
        }
        Paciente p = new Paciente();
        p.setId(idUsuario);
        p.setFechaNacimiento(fechaNac);
        p.setDomicilio(domicilio);
        p.setIdObraSocial(idObraSocial);

        if (!pacienteDAO.insertar(p)) {
            return Respuesta.error("No se pudieron guardar los datos del paciente.");
        }
        HistoriaClinica h = new HistoriaClinica();
        h.setIdPaciente(idUsuario);
        h.setFechaCreacion(LocalDate.now());
        historiaDAO.insertar(h);

        return Respuesta.ok("Datos completados y historia clinica creada.");
    }

    public Respuesta actualizarDatos(int idUsuario, LocalDate fechaNac, String domicilio, int idObraSocial) {
        Paciente p = new Paciente();
        p.setId(idUsuario);
        p.setFechaNacimiento(fechaNac);
        p.setDomicilio(domicilio);
        p.setIdObraSocial(idObraSocial);
        if (pacienteDAO.actualizar(p)) {
            return Respuesta.ok("Datos actualizados correctamente.");
        }
        return Respuesta.error("No se pudieron actualizar los datos.");
    }


    public Respuesta actualizarContacto(Usuario u) {
        if (usuarioDAO.actualizar(u)) {
            return Respuesta.ok("Datos de contacto actualizados.");
        }
        return Respuesta.error("No se pudieron actualizar los datos de contacto.");
    }

    public Paciente buscarDatos(int idUsuario) {
        return pacienteDAO.buscarPorId(idUsuario);
    }


    public List<Resultado> listarResultados(int idPaciente) {
        return resultadoDAO.listarAutorizadosPaciente(idPaciente);
    }
}
