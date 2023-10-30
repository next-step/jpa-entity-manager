package hibernate;

import hibernate.entity.persistencecontext.EntityKey;

public class EntityEntry {

    private final EntityKey entityKey;
    private Status status;

    public EntityEntry(EntityKey entityKey, Status status) {
        this.entityKey = entityKey;
        this.status = status;
    }
}
