package persistence.entity.event;

import persistence.entity.EntityPersister;

public class UpdateEvent<T, ID> implements Event<T, ID> {

    private final ID id;
    private final T entity;

    public UpdateEvent(ID id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    @Override
    public void excetute(EntityPersister entityPersister) {
        entityPersister.update(entity, id);
    }
}
