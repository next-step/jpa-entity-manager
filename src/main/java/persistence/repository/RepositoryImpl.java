package persistence.repository;

import persistence.sql.dml.exception.InvalidDeleteNullPointException;
import persistence.sql.entity.manager.EntityManger;

import java.util.List;
import java.util.Optional;

public class RepositoryImpl<T, K> implements Repository<T, K> {

    private final EntityManger<T> entityManger;
    private final Class<T> clazz;

    public RepositoryImpl(final EntityManger<T> entityManger,
                          final Class<T> clazz) {
        this.entityManger = entityManger;
        this.clazz = clazz;
    }

    @Override
    public List<T> findAll() {
        return entityManger.findAll(clazz);
    }

    @Override
    public Optional<T> findById(Object id) {
        return Optional.ofNullable(entityManger.find(clazz, id));
    }

    @Override
    public T save(T t) {
        entityManger.persist(t);
        return t;
    }

    @Override
    public void deleteAll() {
        entityManger.removeAll(clazz);
    }

    @Override
    public void deleteById(Object id) {
        T t = entityManger.find(clazz, id);
        if (t == null) {
            throw new InvalidDeleteNullPointException();
        }

        entityManger.remove(t);
    }
}