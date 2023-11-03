package persistence.entity;

import persistence.sql.entity.EntityData;
import util.ReflectionUtil;

import java.util.Objects;

public class EntityKey {

    private final Class<?> entityClass;
    private final Object id;

    public EntityKey(Object entity) {
        this.entityClass = entity.getClass();
        this.id = ReflectionUtil.getValueFrom(
                new EntityData(entityClass).getPrimaryKey().getField(),
                entity
        );
    }

    public EntityKey(Class<?> entityClass, Object id) {
        this.entityClass = entityClass;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(entityClass, entityKey.entityClass) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, id);
    }

}
