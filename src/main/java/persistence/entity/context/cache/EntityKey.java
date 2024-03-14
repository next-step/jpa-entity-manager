package persistence.entity.context.cache;

import java.util.Objects;

public class EntityKey {

    private final Object identifier;

    private final String className;

    public EntityKey(final Object identifier, final String className) {
        this.identifier = identifier;
        this.className = className;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (!(object instanceof EntityKey)) return false;
        final EntityKey that = (EntityKey) object;
        return Objects.equals(className, that.className) && Objects.equals(that.identifier, identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, className);
    }
}
