package dao;

import db.Conexion;
import modelo.Medico;
import modelo.Rol;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO implements DAO<Medico> {

    @Override
    public boolean insertar(Medico m) {
        String sql = "INSERT INTO medico (id_usuario, matricula, id_especialidad) VALUES (?, ?, ?)";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.setString(2, m.getMatricula());
            ps.setInt(3, m.getIdEspecialidad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar medico: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(Medico m) {
        String sql = "UPDATE medico SET matricula = ?, id_especialidad = ? WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getMatricula());
            ps.setInt(2, m.getIdEspecialidad());
            ps.setInt(3, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar medico: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM medico WHERE id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar medico: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Medico buscarPorId(int idUsuario) {
        String sql = "SELECT u.id_usuario, u.dni, u.nombre, u.apellido, u.email, u.telefono, u.activo, " +
                "m.matricula, m.id_especialidad " +
                "FROM medico m JOIN usuario u ON m.id_usuario = u.id_usuario " +
                "WHERE m.id_usuario = ?";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar medico: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Medico> listarTodos() {
        return listar(false);
    }
    public List<Medico> listarActivos() {
        return listar(true);
    }

    private List<Medico> listar(boolean soloActivos) {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.dni, u.nombre, u.apellido, u.email, u.telefono, u.activo, " +
                "m.matricula, m.id_especialidad " +
                "FROM medico m JOIN usuario u ON m.id_usuario = u.id_usuario " +
                (soloActivos ? "WHERE u.activo = TRUE " : "") +
                "ORDER BY u.apellido, u.nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar medicos: " + e.getMessage());
        }
        return lista;
    }

    public List<Usuario> listarPacientesDelMedico(int idMedico) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT u.id_usuario, u.dni, u.nombre, u.apellido " +
                "FROM turno t JOIN usuario u ON t.id_paciente = u.id_usuario " +
                "WHERE t.id_medico = ? ORDER BY u.apellido, u.nombre";
        Connection con = Conexion.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id_usuario"));
                    u.setDni(rs.getString("dni"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setRol(Rol.PACIENTE);
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pacientes del medico: " + e.getMessage());
        }
        return lista;
    }

    private Medico mapear(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setId(rs.getInt("id_usuario"));
        m.setDni(rs.getString("dni"));
        m.setNombre(rs.getString("nombre"));
        m.setApellido(rs.getString("apellido"));
        m.setEmail(rs.getString("email"));
        m.setTelefono(rs.getString("telefono"));
        m.setActivo(rs.getBoolean("activo"));
        m.setRol(Rol.MEDICO);
        m.setMatricula(rs.getString("matricula"));
        m.setIdEspecialidad(rs.getInt("id_especialidad"));
        return m;
    }
}
