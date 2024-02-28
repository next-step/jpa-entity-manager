package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    Optional<Object> getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Long id);
}
