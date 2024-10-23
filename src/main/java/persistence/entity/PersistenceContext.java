package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    <T, ID> void addEntity(ID id, T entity);

    <T, ID> void removeEntity(ID id, Class<T> entityType);

}
