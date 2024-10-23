package jpa;

import java.util.Objects;

public class EntityInfo<T> {

    private final Class<T> clazz;
    private final Object id;

    public EntityInfo(Class<T> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof EntityInfo) {
            return false;
        }

        EntityInfo<?> otherEntityInfo = (EntityInfo<?>) obj;
        return clazz.equals(otherEntityInfo.clazz) && id.equals(otherEntityInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
