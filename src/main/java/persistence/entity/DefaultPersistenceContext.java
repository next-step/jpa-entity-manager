package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, EntityEntry> entityInstanceMap = new HashMap<>();
    private final Map<EntityKey, Object> entitySnapShotMap = new HashMap<>();
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private static DefaultPersistenceContext INSTANCE;

    private DefaultPersistenceContext(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultPersistenceContext of(JdbcTemplate jdbcTemplate) {
        if (nonNull(INSTANCE)) {
            return INSTANCE;
        }
        INSTANCE = new DefaultPersistenceContext(EntityPersister.of(jdbcTemplate), EntityLoader.of(jdbcTemplate));
        return INSTANCE;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, Long entityId) {
        EntityKey entityKey = EntityKey.of(clazz, entityId);
        EntityEntry entityEntry = entityInstanceMap.get(entityKey);
        if (Objects.isNull(entityEntry)) {
            Object entity = entityLoader.selectOne(clazz, entityId);
            entityEntry = EntityEntry.from(entity);
            entityInstanceMap.put(entityKey, entityEntry);
            entityEntry.manage();
        }
        return clazz.cast(entityEntry.getEntity());
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey entityKey = EntityKey.from(entity);
        EntityEntry entityEntry = entityInstanceMap.get(entityKey);
        if (Objects.nonNull(entityEntry)) {
            validateReadOnly(entityEntry);
            entityEntry.save();
            entityPersister.update(entity);
        }
        if (Objects.isNull(entityEntry)) {
            entityPersister.insert(entity);
            entityKey = EntityKey.from(entity);
            entityEntry = EntityEntry.from(entity);
        }
        entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
        entityInstanceMap.put(entityKey, entityEntry);
        entityEntry.manage();
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = EntityKey.from(entity);
        EntityEntry entityEntry = entityInstanceMap.get(entityKey);
        validateReadOnly(entityEntry);
        entityEntry.delete();
        entityInstanceMap.remove(entityKey);
        entitySnapShotMap.remove(entityKey);
        entityPersister.delete(entity);
        entityEntry.gone();
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = EntityKey.of(entity.getClass(), id);
        return entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
    }

    private void validateReadOnly(EntityEntry entityEntry) {
        if (entityEntry.isReadOnly()) {
            throw new IllegalStateException("해당 엔티티는 읽기 전용입니다. 쓰기작업을 실행할 수 없습니다.");
        }
    }

}
