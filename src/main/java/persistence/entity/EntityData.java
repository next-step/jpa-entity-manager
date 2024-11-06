package persistence.entity;

import java.util.Objects;

public class EntityData {

    private final Object id;
    private final Class<?> entityClass;;
    private final Object entity;

    public EntityData(Object id, Class<?> entityClass, Object entity) {
        this.id = id;
        this.entityClass = entityClass;
        this.entity = entity;
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
