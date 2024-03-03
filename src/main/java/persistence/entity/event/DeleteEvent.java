package persistence.entity.event;


public class DeleteEvent<T, ID> {
    private final ID id;
    private final T entity;

    public DeleteEvent(ID id, T entity) {
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
