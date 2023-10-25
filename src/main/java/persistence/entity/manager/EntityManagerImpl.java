package persistence.entity.manager;

import jakarta.persistence.Id;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;


    private EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;

    }

    public static EntityManagerImpl of(EntityPersister entityPersister, EntityLoader entityLoader) {
        return new EntityManagerImpl(entityPersister, entityLoader);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        return entityLoader.load(clazz, id);
    }

    @Override
    public <T> T persist(T instance) {
        return entityPersister.insert(instance);
    }

    @Override
    public <T> void remove(T entity) {
        entityPersister.remove(entity, getEntityId(entity));
    }

    private <T> String getEntityId(T entity) {
        Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ID 필드를 찾을 수 없습니다."));

        idField.setAccessible(true);

        try {
            Object idValue = idField.get(entity);

            assert idValue != null;

            return String.valueOf(idValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
