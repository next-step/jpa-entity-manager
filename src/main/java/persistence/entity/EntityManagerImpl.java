package persistence.entity;

import database.mapping.EntityClass;
import database.mapping.EntityMetadata;
import jdbc.JdbcTemplate;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    private EntityManagerImpl(PersistenceContext persistenceContext, EntityLoader entityLoader,
                              EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
    }

    public static EntityManagerImpl from(JdbcTemplate jdbcTemplate) {
        return new EntityManagerImpl(
                new PersistenceContextImpl(),
                new EntityLoader(jdbcTemplate),
                new EntityPersister(jdbcTemplate));
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object cached = persistenceContext.getEntity(clazz, id);
        if (Objects.isNull(cached)) {
            Optional<Object> load = entityLoader.load(clazz, id);
            if (load.isPresent()) {
                Object object = load.get();
                persistenceContext.addEntity(object);
            }
        }
        return (T) persistenceContext.getEntity(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        Class<?> clazz = entity.getClass();

        Long id = getRowId(entity);
        if (isInsertOperation(clazz, id)) {
            insertEntity(entity, clazz);
            return;
        }
        updateEntity(entity, clazz, id);
    }

    private void insertEntity(Object entity, Class<?> clazz) {
        Long newId = entityPersister.insert(clazz, entity);
        Object load = entityLoader.load(clazz, newId).get();
        persistenceContext.addEntity(load);
    }

    private void updateEntity(Object entity, Class<?> clazz, Long id) {
        Object oldEntity = find(clazz, id);
        Map<String, Object> diff = EntitySnapshot.of(oldEntity).diff(EntitySnapshot.of(entity));
        if (!diff.isEmpty()) {
            entityPersister.update(clazz, id, diff);
            persistenceContext.addEntity(entity);
        }
    }

    /**
     * id 가 없으면 insert, id 가 있으면 insert 일수도 아닐 수도 있다.
     * 비즈니스 로직에서 잘 넣어줬을 거라고 생각하고, 현재 퍼스트레벨 캐시에 있는지만 확인한다.
     */
    private boolean isInsertOperation(Class<?> clazz, Long id) {
        return id == null || persistenceContext.getEntity(clazz, id) == null;
    }

    @Override
    public void remove(Object entity) {
        if (persistenceContext.isRemoved(entity)) {
            // 아무것도 안함
            return;
        }
        Class<?> clazz = entity.getClass();
        Long id = getRowId(entity);
        find(clazz, id);
        entityPersister.delete(clazz, id);
        persistenceContext.removeEntity(entity);
    }

    private static Long getRowId(Object entity) {
        Class<?> clazz = entity.getClass();
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata entityMetadata = entityClass.getMetadata();

        return entityMetadata.getPrimaryKeyValue(entity);
    }
}
