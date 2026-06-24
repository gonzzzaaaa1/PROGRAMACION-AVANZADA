package servicio;

import dao.MedicoDAO;
import dao.UsuarioDAO;
import modelo.Medico;
import modelo.Usuario;

import java.util.List;

public class UsuarioServicio {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    public Usuario login(String dni, String contrasenia) {
        return usuarioDAO.validarLogin(dni, contrasenia);
    }

    public Respuesta registrarUsuario(Usuario u) {
        if (usuarioDAO.insertar(u)) {
            return Respuesta.ok("Usuario registrado correctamente (ID " + u.getId() + ").");
        }
        return Respuesta.error("No se pudo registrar el usuario. Revisa que el DNI no este repetido.");
    }

    public Respuesta registrarMedico(Medico m) {
        if (!usuarioDAO.insertar(m)) {
            return Respuesta.error("No se pudo registrar el medico. Revisa que el DNI no este repetido.");
        }
        if (medicoDAO.insertar(m)) {
            return Respuesta.ok("Medico registrado correctamente (ID " + m.getId() + ").");
        }
        return Respuesta.error("Se creo el usuario pero fallo el alta de los datos de medico.");
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public Respuesta cambiarEstado(int idUsuario, boolean activo) {
        if (usuarioDAO.cambiarEstado(idUsuario, activo)) {
            return Respuesta.ok(activo ? "Usuario activado." : "Usuario desactivado.");
        }
        return Respuesta.error("No se pudo cambiar el estado del usuario.");
    }
}
