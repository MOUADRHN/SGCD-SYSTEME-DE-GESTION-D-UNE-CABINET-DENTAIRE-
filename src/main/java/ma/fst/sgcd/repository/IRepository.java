package ma.fst.sgcd.repository;
import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {
    Optional<T> findById(ID id);
    List<T>     findAll();
    T           save(T entity);
    boolean     update(T entity);
    boolean     delete(ID id);
}
