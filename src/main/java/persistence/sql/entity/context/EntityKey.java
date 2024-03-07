package persistence.sql.entity.context;

import java.util.Objects;

public class EntityKey {

    private final String clazzName;
    private final Object id;

    public EntityKey(final String clazzName, final Object id) {
        this.clazzName = clazzName;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(clazzName, entityKey.clazzName) && Objects.equals(id.toString(), entityKey.id.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazzName, id.toString());
    }
}
