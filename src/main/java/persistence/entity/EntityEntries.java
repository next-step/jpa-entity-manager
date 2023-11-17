package persistence.entity;

import persistence.sql.ddl.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class EntityEntries {

    private final Map<Class<?>, Map<String, EntityEntry>> entityEntries = new HashMap<>();

    public void addOrChange(Object entity, Status status) {
        Class<?> entityClass = entity.getClass();
        String id = EntityMetadata.of(entityClass).getIdColumnValue(entity);

        if (!entityEntries.containsKey(entityClass)) {
            entityEntries.put(entityClass, new HashMap<>());
        }

        Map<String, EntityEntry> entries = entityEntries.get(entityClass);

        if (!entries.containsKey(id)) {
            entries.put(id, new EntityEntry(status));
        } else {
            entries.get(id).setStatus(status);
        }
    }

    Map<Class<?>, Map<String, EntityEntry>> getEntityEntries() {
        return entityEntries;
    }

    public Status getStatus(Object entity) {
        Class<?> entityClass = entity.getClass();
        String id = EntityMetadata.of(entityClass).getIdColumnValue(entity);

        if (!entityEntries.containsKey(entityClass)) {
            return Status.NEW;
        }

        Map<String, EntityEntry> entries = entityEntries.get(entityClass);

        if (!entries.containsKey(id)) {
            return Status.NEW;
        }

        return entries.get(id).getStatus();
    }
}
