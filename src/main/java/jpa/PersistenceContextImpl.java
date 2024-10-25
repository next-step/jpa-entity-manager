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
    public Object get(EntityInfo<?> entityInfo) {
        return entityMap.get(entityInfo);
    }

    @Override
    public void remove(Object entity) {
        entityMap.remove(makeEntityInfo(entity));
    }

    @Override
    public boolean contain(EntityInfo<?> entityInfo) {
        return entityMap.containsKey(entityInfo);
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
