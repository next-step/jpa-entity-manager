package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.ddl.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;

import static persistence.entity.Status.LOADING;
import static persistence.entity.Status.SAVING;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;

    private final Dialect dialect;

    public SimpleEntityManager(EntityPersister entityPersister,
                               EntityLoader entityLoader,
                               PersistenceContext persistenceContext,
                               Dialect dialect) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.dialect = dialect;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object persistedEntity = persistenceContext.getEntity(id);
        if (persistedEntity != null) {
            return (T) persistedEntity;
        }

        T entity = entityLoader.findById(clazz, id);
        if (entity == null) {
            throw new RuntimeException("엔티티가 없습니다.");
        }

        persistenceContext.changeEntityStatus(entity, LOADING);
        persistenceContext.addEntity(id, entity);
        // TODO 복사해서 넣는 부분
        persistenceContext.addEntitySnapshot(id, entity);
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        if (hasIdValue(entity)) {
            return merge(entity);
        }

        T persistedEntity = entityPersister.insert(entity);
        persistenceContext.changeEntityStatus(entity, SAVING);
        persistenceContext.addEntity(Long.parseLong(findEntityId(persistedEntity)), persistedEntity);
        // TODO 복사해서 넣는 부분
        persistenceContext.addEntitySnapshot(Long.parseLong(findEntityId(persistedEntity)), persistedEntity);
        return persistedEntity;
    }

    @Override
    public <T> T merge(T entity) {
        Object snapshot = persistenceContext.getDatabaseSnapshot(
                Long.valueOf(findEntityId(entity)),
                entity
        );

        if (isDirty(entity)) {
            entityPersister.update(entity, snapshot);
            persistenceContext.addEntitySnapshot(Long.parseLong(findEntityId(entity)), entity);
        }

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        persistenceContext.removeEntity(entity);
        // TODO entityPersister.remove() 익셉션 테스트가 위에서 다 익셉션 먼저 발생해서 테스트하기 힘드네.
        entityPersister.remove(entity, findEntityId(entity));
    }

    private <T> boolean isDirty(T entity) {
        Object snapshot = persistenceContext.getDatabaseSnapshot(
                Long.valueOf(findEntityId(entity)),
                entity
        );
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass());

        return entityMetadata.hasDifferentValue(entity, snapshot);
    }

    private <T> boolean hasIdValue(T entity) {
        Object id = extractId(entity);
        return id != null;
    }

    private <T> String findEntityId(T entity) {
        Object idValue = extractId(entity);

        if (idValue == null) {
            throw new RuntimeException("ID 값이 없습니다.");
        }

        return String.valueOf(idValue);
    }

    private <T> Object extractId(T entity) {
        Field field = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ID 필드가 없습니다."));
        field.setAccessible(true);

        return getIdValue(entity, field);
    }

    private <T> Object getIdValue(T entity, Field field) {
        Object idValue;

        try {
            idValue = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field 값을 읽어오는데 실패했습니다.");
        }

        return idValue;
    }

}
