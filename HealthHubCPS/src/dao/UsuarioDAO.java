package dao;

import db.Conexion;
import modelo.Rol;
import modelo.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements DAO<Usuario> {

    public Usuario validarLogin(String dni, String contrasenia) {
        String sql = "SELECT id_usuario, dni, nombre, apellido, email, telefono, rol, activo " +
                "FROM usuario WHERE dni = ? AND contrasenia = ? AND activo = TRUE";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, contrasenia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (dni, contrasenia, nombre, apellido, email, telefono, rol, fecha_alta) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?::rol_usuario, ?) RETURNING id_usuario";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getDni());
            ps.setString(2, u.getContrasenia());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellido());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getTelefono());
            ps.setString(7, u.getRol().name());
            ps.setDate(8, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u.setId(rs.getInt("id_usuario"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, email = ?, telefono = ? WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefono());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        return cambiarEstado(idUsuario, false);
    }

    public boolean cambiarEstado(int idUsuario, boolean activo) {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT id_usuario, dni, nombre, apellido, email, telefono, rol, activo " +
                "FROM usuario WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, dni, nombre, apellido, email, telefono, rol, activo " +
                "FROM usuario ORDER BY activo DESC, apellido, nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setDni(rs.getString("dni"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        u.setTelefono(rs.getString("telefono"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}