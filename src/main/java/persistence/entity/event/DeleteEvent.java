package persistence.entity.event;

import persistence.entity.EntityPersister;

public class DeleteEvent<T, ID> implements Event<T, ID> {
    private final ID id;
    private final T entity;

    public DeleteEvent(ID id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    @Override
    public void excetute(EntityPersister entityPersister) {
        entityPersister.delete(entity, id);
    }

}
