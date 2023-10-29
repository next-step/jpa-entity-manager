package persistence.entity.entry;

public class EntityWrap {

    private final Object instance;
    private final EntityEntry entry;

    public EntityWrap(Object instance, EntityEntry entry) {
        this.instance = instance;
        this.entry = entry;
    }

    public Object getInstance() {
        return instance;
    }

    public EntityEntry getEntry() {
        return entry;
    }
}
