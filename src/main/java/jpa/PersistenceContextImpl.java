package jpa;

import org.jetbrains.annotations.NotNull;
import persistence.sql.model.EntityId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityInfo<?>, Object> entityMap = new HashMap<>();

    public PersistenceContextImpl() {
    }

    @Override
    public void add(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);

        if (entityMap.containsKey(entityInfo)) {
            return;
        }
        entityMap.put(entityInfo, entity);
    }

    @Override
    public <T> T get(Class<T> clazz, Long id) {
        EntityInfo<?> entityInfo = new EntityInfo<>(clazz, id);
        Object object = entityMap.get(entityInfo);
        if (object == null) {
            return null;
        }
        return clazz.cast(object);
    }

    @Override
    public void remove(Object entity) {
        entityMap.remove(makeEntityInfo(entity));
    }

    @Override
    public void update(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        entityMap.put(entityInfo, entity);
    }

    private EntityInfo<?> makeEntityInfo(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        Long idValue = entityId.getIdValue(entity);
        return new EntityInfo<>(entity.getClass(), idValue);
    }
}
