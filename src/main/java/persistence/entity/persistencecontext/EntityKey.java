package persistence.entity.persistencecontext;

import persistence.PrimaryKey;

import java.util.Objects;
public class EntityKey {

    private final String className;
    private final Long id;

    public EntityKey(Class<?> clazz, Long id) {
        this.className = clazz.getName();
        this.id = id;
    }

    public <T> EntityKey(T entity) {
        this.className = entity.getClass().getName();
        this.id = new PrimaryKey(entity).value();
    }

    public String getClassName() {
        return className;
    }

    public Long getId() {
        return id;
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
