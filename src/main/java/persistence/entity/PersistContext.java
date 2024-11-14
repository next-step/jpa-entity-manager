package persistence.entity;

import java.util.Optional;

public interface PersistContext {
    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    void addEntity(Object entity);
}
