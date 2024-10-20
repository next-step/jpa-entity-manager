package persistence.context.impl;

import persistence.context.PersistenceContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<Class<?>, Map<?, Object>> context = new HashMap<>();

    @Override
    public <T, ID> T get(Class<T> entityType, ID id) {
        if (context.containsKey(entityType) && context.get(entityType).containsKey(id)) {
            return entityType.cast(context.get(entityType).get(id));
        } else {
            return null;
        }
    }

    @Override
    public <T> Collection<T> getAll(Class<T> entityType) {
        if (context.containsKey(entityType)) {
            return context.get(entityType).values().stream()
                    .map(entityType::cast).toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> T add(ID id, T entity) {
        Map<ID, Object> entityMap = (Map<ID, Object>) context.computeIfAbsent(entity.getClass(), k -> new HashMap<>());

        if (entityMap.containsKey(id)) {
            return (T) entityMap.get(id);
        }

        return (T) entityMap.put(id, entity);
    }
}
