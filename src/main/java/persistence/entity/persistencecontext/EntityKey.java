package persistence.entity.persistencecontext;

import java.util.Objects;

public class EntityKey {
    private final String clasName;
    private final Long id;

    public EntityKey(Class<?> clazz, Long id) {
        this.clasName = clazz.getSimpleName();
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey key = (EntityKey) o;
        return Objects.equals(clasName, key.clasName) && Objects.equals(id, key.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clasName, id);
    }
}
