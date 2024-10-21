package persistence;

public class EntityInfo<T> {

    private Object id;
    private Class<T> clazz;

    private EntityInfo(Object id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public static <T> EntityInfo<T> createEntityInfo(Object id, Class<T> clazz) {
        return new EntityInfo<>(id, clazz);
    }

    public Object getId() {
        return id;
    }
}
