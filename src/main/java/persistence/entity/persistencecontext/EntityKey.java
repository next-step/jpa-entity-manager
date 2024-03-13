package persistence.entity.persistencecontext;

import java.util.Objects;

import static persistence.entity.generator.PrimaryKeyValueGenerator.primaryKeyValue;

public class EntityKey {
    private final String className;
    private final Long id;

    public EntityKey(Class<?> clazz, Long id) {
        this.className = clazz.getName();
        this.id = id;
    }

    public EntityKey (Object entity) {
        this.className = entity.getClass().getName();
        this.id = primaryKeyValue(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey key = (EntityKey) o;
        return Objects.equals(className, key.className) && Objects.equals(id, key.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, id);
    }
}
