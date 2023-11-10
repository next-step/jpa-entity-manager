package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.ddl.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;

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
        persistenceContext.addEntity(id, entity);
        persistenceContext.addEntitySnapshot(id, entity);
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        T persistedEntity = entityPersister.insert(entity);
        persistenceContext.addEntity(Long.parseLong(findEntityId(persistedEntity)), persistedEntity);
        persistenceContext.addEntitySnapshot(Long.parseLong(findEntityId(persistedEntity)), persistedEntity);
        return persistedEntity;
    }

    @Override
    public <T> T merge(T entity) {
        Object snapshot = persistenceContext.getDatabaseSnapshot(
                Long.valueOf(findEntityId(entity)),
                entity
        );

        entityPersister.update(entity, snapshot);
        persistenceContext.addEntitySnapshot(Long.parseLong(findEntityId(entity)), entity);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        persistenceContext.removeEntity(entity);
        // TODO entityPersister.remove() 익셉션 테스트가 위에서 다 익셉션 먼저 발생해서 테스트하기 힘드네.
        entityPersister.remove(entity, findEntityId(entity));
    }

    private <T> String findEntityId(T entity) {
        Field field = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ID 필드가 없습니다."));
        field.setAccessible(true);

        Object value = null;

        // TODO 이런 부분도 테스트를 만들고 싶은데
        try {
            value = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field 값을 읽어오는데 실패했습니다.");
        }

        if (value == null) {
            throw new RuntimeException("ID 값이 없습니다.");
        }

        return String.valueOf(value);
    }

}
