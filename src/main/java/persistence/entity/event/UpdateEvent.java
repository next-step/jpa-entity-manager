package persistence.entity.event;

public class UpdateEvent<T, ID> {

    private final ID id;
    private final T entity;

    public UpdateEvent(ID id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    public ID getId() {
        return id;
    }

    public T getEntity() {
        return entity;
    }
}
