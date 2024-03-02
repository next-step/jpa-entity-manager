package persistence.entity;

import persistence.sql.domain.DatabasePrimaryColumn;
import persistence.sql.domain.DatabaseTable;

import java.util.Objects;

public class EntityCacheKey {

    private final Class<?> entityClazz;

    private final Object id;

    public EntityCacheKey(Class<?> entityClazz, Object id) {
        this.entityClazz = entityClazz;
        this.id = id;
    }

    public EntityCacheKey(Object entity) {
        DatabasePrimaryColumn primaryColumn = new DatabaseTable(entity).getPrimaryColumn();
        this.entityClazz = entity.getClass();
        this.id = primaryColumn.getColumnValueByJavaType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityCacheKey)) return false;
        EntityCacheKey that = (EntityCacheKey) o;
        return Objects.equals(entityClazz, that.entityClazz) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClazz, id);
    }
}
