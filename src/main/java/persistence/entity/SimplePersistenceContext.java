package persistence.entity;

import persistence.sql.ddl.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<String, Object> entities = new HashMap<>();
    private final Map<String, Object> entitySnapshots = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entities.get(String.valueOf(id));
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        if (entitySnapshots.containsKey(String.valueOf(id))) {
            return entitySnapshots.get(String.valueOf(id));
        }

        entitySnapshots.put(String.valueOf(id), entity);
        return entitySnapshots.get(String.valueOf(id));
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entities.put(String.valueOf(id), entity);
    }

    @Override
    public void addEntitySnapshot(Long id, Object entity) {
        entitySnapshots.put(String.valueOf(id), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String id = EntityMetadata.of(entity.getClass()).getIdColumnValue(entity);
        entities.remove(id);
    }

}
