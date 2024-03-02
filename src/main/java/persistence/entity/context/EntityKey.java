package persistence.entity.context;

import java.util.Objects;

class EntityKey {
    private final String entityClass;
    private final Long id;

    public EntityKey(String entityClass, Long id) {
        this.entityClass = entityClass;
        this.id = id;
    }

    public static EntityKey of(Class<?> entityClass, Long id) {
        return new EntityKey(entityClass.getName(), id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(entityClass, entityKey.entityClass) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, id);
    }
}
