package persistence.entity.persistencecontext;

import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext<E> implements PersistenceContext<E> {
    private final Map<EntityPersistIdentity, E> persistenceContextMap = new HashMap<>();
    private final Map<EntityPersistIdentity, EntitySnapShot> snapshotContextMap = new HashMap<>();

    @Override
    public E getEntity(EntityPersistIdentity id) {
        return persistenceContextMap.get(id);
    }

    @Override
    public void addEntity(EntityPersistIdentity id, E entity) {
        persistenceContextMap.put(id, entity);
    }

    @Override
    public void removeEntity(E entity) {
        Class<E> entityClass = (Class<E>) entity.getClass();
        EntityColumn<E, ?> entityIdColumn = new EntityTable<>(entityClass).getIdColumn();

        EntityPersistIdentity persistId = new EntityPersistIdentity(entityClass, entityIdColumn.getValue(entity));

        persistenceContextMap.remove(persistId);
    }

    @Override
    public EntitySnapShot getDatabaseSnapshot(EntityPersistIdentity id, E entity) {
        return snapshotContextMap.put(id, EntitySnapShot.fromEntity(entity));
    }
}
