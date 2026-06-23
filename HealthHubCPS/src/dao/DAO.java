package dao;

import java.util.List;


public interface DAO<T> {

    boolean insertar(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(int id);

    T buscarPorId(int id);

    List<T> listarTodos();
}
