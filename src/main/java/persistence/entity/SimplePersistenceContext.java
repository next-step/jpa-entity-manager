package persistence.entity;

import persistence.sql.ddl.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<String, Object> entityMap = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entityMap.get(String.valueOf(id));
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entityMap.put(String.valueOf(id), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String id = EntityMetadata.of(entity.getClass()).getIdColumnValue(entity);
        entityMap.remove(id);
    }

}
