package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    public SimpleEntityManager(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityPersister.findById(clazz, id);
    }

    @Override
    public <T> T persist(T entity) {
        return entityPersister.insert(entity);
    }

    @Override
    public <T> void remove(T entity) {
        entityPersister.remove(entity, findEntityId(entity));
    }

    private <T> String findEntityId(T entity) {
        Field field = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ID 필드가 없습니다."));
        field.setAccessible(true);

        try {
            Object value = field.get(entity);
            return String.valueOf(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
