package persistence.entity.domain;

import persistence.sql.ddl.domain.Columns;

import java.util.Objects;

public class EntityKey {

    private final Class<?> clazz;
    private final Object id;

    public EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public EntityKey(Object entity) {
        this.clazz = entity.getClass();
        Columns columns = new Columns(entity.getClass());
        this.id = columns.getOriginValue(entity);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(clazz, entityKey.clazz) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }

}
