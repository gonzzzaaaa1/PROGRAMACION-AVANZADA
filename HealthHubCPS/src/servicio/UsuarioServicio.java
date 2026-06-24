package servicio;

import dao.MedicoDAO;
import dao.UsuarioDAO;
import modelo.Medico;
import modelo.Usuario;

import java.util.List;

/**
 * Logica de usuarios: login y alta/gestion de usuarios.
 */
public class UsuarioServicio {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    /** Intenta iniciar sesion. Devuelve el usuario o null si las credenciales no son validas. */
    public Usuario login(String dni, String contrasenia) {
        return usuarioDAO.validarLogin(dni, contrasenia);
    }

    /** Alta de un usuario sin datos extra (paciente o administrador). */
    public Respuesta registrarUsuario(Usuario u) {
        if (usuarioDAO.insertar(u)) {
            return Respuesta.ok("Usuario registrado correctamente (ID " + u.getId() + ").");
        }
        return Respuesta.error("No se pudo registrar el usuario. Revisa que el DNI no este repetido.");
    }

    /** Alta de un medico: crea el usuario y luego sus datos de medico. */
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

    /** Activa o desactiva un usuario (gestion de estado del admin). */
    public Respuesta cambiarEstado(int idUsuario, boolean activo) {
        if (usuarioDAO.cambiarEstado(idUsuario, activo)) {
            return Respuesta.ok(activo ? "Usuario activado." : "Usuario desactivado.");
        }
        return Respuesta.error("No se pudo cambiar el estado del usuario.");
    }
}
