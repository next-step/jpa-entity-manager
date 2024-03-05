package persistence.repository;

import persistence.sql.dml.exception.InvalidDeleteNullPointException;
import persistence.sql.entity.manager.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositoryImpl<T, K> implements Repository<T, K> {

    private final EntityManager entityManager;
    private final Class<T> clazz;

    public RepositoryImpl(final EntityManager entityManager,
                          final Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    @Override
    public List<T> findAll() {
        return entityManager.findAll(clazz);
    }

    @Override
    public Optional<T> findById(Object id) {
        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    @Override
    public T save(T t) {
        entityManager.persist(t);
        return t;
    }

    @Override
    public void deleteAll() {
        entityManager.removeAll(clazz);
    }

    @Override
    public void deleteById(Object id) {
        T t = entityManager.find(clazz, id);
        if (t == null) {
            throw new InvalidDeleteNullPointException();
        }

        entityManager.remove(t);
    }
}
