package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {
    Optional<Object> getEntity(EntityKey key);

    void addEntity(EntityKey key, Object entity);

    void removeEntity(EntityKey key);

}
