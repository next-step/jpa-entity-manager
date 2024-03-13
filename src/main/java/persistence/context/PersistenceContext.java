package persistence.context;

import persistence.entity.EntityId;

public interface PersistenceContext {
    Object getEntity(EntityId id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);
}
