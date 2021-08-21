package repositories;

import java.util.List;
import java.util.UUID;

public interface Crud<T> {
    boolean create(T o);
    T findById(UUID id);
    List<T> findAll();
    boolean update(T o);
    boolean delete(T o);
}
