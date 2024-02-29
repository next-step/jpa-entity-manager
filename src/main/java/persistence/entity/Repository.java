package persistence.entity;

public interface Repository<T, ID> {

    T save(T entity);
}
