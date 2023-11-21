package persistence.entity;

import persistence.sql.ddl.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

import static persistence.entity.Status.DELETED;
import static persistence.entity.Status.MANAGED;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<String, Object> entities = new HashMap<>();

    private final Map<String, Object> entitySnapshots = new HashMap<>();

    private final EntityEntries entityEntries = new EntityEntries();

    @Override
    public Object getEntity(Long id) {
        return entities.get(String.valueOf(id));
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        if (entitySnapshots.containsKey(String.valueOf(id))) {
            return entitySnapshots.get(String.valueOf(id));
        }

        addEntitySnapshot(id, entity);
        return entitySnapshots.get(String.valueOf(id));
    }

    @Override
    public void addEntity(Long id, Object entity, Status status) {
        entityEntries.addOrChange(entity, status);
        entities.put(String.valueOf(id), entity);
        entityEntries.addOrChange(entity, MANAGED);
    }

    @Override
    public void addEntitySnapshot(Long id, Object entity) {
        entitySnapshots.put(String.valueOf(id), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String id = EntityMetadata.of(entity.getClass()).getIdColumnValue(entity);
        entities.remove(id);
        entitySnapshots.remove(id);
        entityEntries.addOrChange(entity, DELETED);
        // EntityEntries에서도 삭제해야 할까?
    }

    @Override
    public void changeEntityStatus(Object entity, Status status) {
        entityEntries.addOrChange(entity, status);
    }

    @Override
    public Status getEntityStatus(Object entity) {
        return entityEntries.getStatus(entity);
    }
}
