package persistence.entity.manager;

import jakarta.persistence.Id;
import persistence.entity.persister.EntityPersister;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;


    private EntityManagerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    public static EntityManagerImpl of(EntityPersister entityPersister) {
        return new EntityManagerImpl(entityPersister);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        return loadAndManageEntity(clazz, id);
    }

    private <T> T loadAndManageEntity(Class<T> clazz, String id) {
        return entityPersister.findById(clazz, id);
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
