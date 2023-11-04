package persistence.entity;

import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<EntityPersistIdentity, Object> persistenceContextMap = new HashMap<>();

    @Override
    public Object getEntity(EntityPersistIdentity id) {
        return persistenceContextMap.get(id);
    }

    @Override
    public void addEntity(EntityPersistIdentity id, Object entity) {
        persistenceContextMap.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        EntityColumn entityIdColumn = new EntityTable<>(entityClass).getIdColumn();

        EntityPersistIdentity persistId = new EntityPersistIdentity(entityClass, entityIdColumn.getValue(entity));

        persistenceContextMap.remove(persistId);
    }
}
