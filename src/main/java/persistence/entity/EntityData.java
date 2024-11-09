package persistence.entity;

import jakarta.persistence.Id;

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
        Object id = null;
        for (var field : entity.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    id = field.get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

    public Class<?> entityClass() {
        return entityClass;
    }

    public Object entity() {
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
        return Objects.equals(id, that.id) && Objects.equals(entityClass, that.entityClass) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityClass, entity);
    }
}
