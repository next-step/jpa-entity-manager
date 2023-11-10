package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.ddl.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final Dialect dialect;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader, Dialect dialect) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.dialect = dialect;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.findById(clazz, id);
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
