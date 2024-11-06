package persistence.entity;

import jakarta.persistence.Id;

import java.util.Arrays;
import java.util.Objects;

public class EntityData {

    private Object id;
    private final Class<?> entityClass;;
    private final Object entity;

    public EntityData(Object id, Class<?> entityClass, Object entity) {
        this.id = id;
        this.entityClass = entityClass;
        this.entity = entity;
    }

    public EntityData(Object entity) {
        this.id = resolveId(entity);
        this.entityClass = entity.getClass();
        this.entity = entity;
    }

    private Object resolveId (Object entity){

        Arrays.stream(entity.getClass().getDeclaredFields()).map(field -> {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalArgumentException("Entity must have a field annotated with @Id");
        }).findFirst().orElseThrow(() -> new IllegalArgumentException("Entity must have a field annotated with @Id"));
        return entity;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityData that = (EntityData) o;
        return Objects.equals(id, that.id) && Objects.equals(entityClass, that.entityClass) && Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityClass, entity);
    }
}
