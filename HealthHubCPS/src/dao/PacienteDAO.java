package dao;

import db.Conexion;
import modelo.Paciente;
import modelo.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO implements DAO<Paciente> {

    @Override
    public boolean insertar(Paciente p) {
        String sql = "INSERT INTO paciente (id_usuario, fecha_nacimiento, domicilio, id_obra_social) " +
                "VALUES (?, ?, ?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setDate(2, Date.valueOf(p.getFechaNacimiento()));
            ps.setString(3, p.getDomicilio());
            ps.setInt(4, p.getIdObraSocial());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar paciente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Paciente p) {
        String sql = "UPDATE paciente SET fecha_nacimiento = ?, domicilio = ?, id_obra_social = ? " +
                "WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(p.getFechaNacimiento()));
            ps.setString(2, p.getDomicilio());
            ps.setInt(3, p.getIdObraSocial());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar paciente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM paciente WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar paciente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Paciente buscarPorId(int idUsuario) {
        String sql = "SELECT u.id_usuario, u.dni, u.nombre, u.apellido, u.email, u.telefono, u.activo, " +
                "p.fecha_nacimiento, p.domicilio, p.id_obra_social " +
                "FROM paciente p JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "WHERE p.id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar paciente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.dni, u.nombre, u.apellido, u.email, u.telefono, u.activo, " +
                "p.fecha_nacimiento, p.domicilio, p.id_obra_social " +
                "FROM paciente p JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "ORDER BY u.apellido, u.nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pacientes: " + e.getMessage());
        }
        return lista;
    }

    /** Indica si el usuario ya cargo sus datos de paciente. */
    public boolean tieneDatos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM paciente WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar datos de paciente: " + e.getMessage());
        }
        return false;
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id_usuario"));
        p.setDni(rs.getString("dni"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setEmail(rs.getString("email"));
        p.setTelefono(rs.getString("telefono"));
        p.setActivo(rs.getBoolean("activo"));
        p.setRol(Rol.PACIENTE);
        Date f = rs.getDate("fecha_nacimiento");
        if (f != null) p.setFechaNacimiento(f.toLocalDate());
        p.setDomicilio(rs.getString("domicilio"));
        p.setIdObraSocial(rs.getInt("id_obra_social"));
        return p;
    }
}
