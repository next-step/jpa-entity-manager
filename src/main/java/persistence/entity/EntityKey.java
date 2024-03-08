package persistence.entity;

public class EntityKey {

    private final Class<?> clazz;

    private final Object id;

    public EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey entityKey = (EntityKey) o;

        if (!clazz.equals(entityKey.clazz)) return false;
        return id.equals(entityKey.id);
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
