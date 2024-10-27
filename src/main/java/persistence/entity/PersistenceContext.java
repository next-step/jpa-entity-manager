package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    <T> void addEntity(T entity);

    <T> void removeEntity(T entity);

}
