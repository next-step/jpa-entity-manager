package persistence.entity.impl;

import persistence.entity.EntityEntry;
import persistence.entity.type.EntityStatus;

public class ImmutableEntityEntry implements EntityEntry {

    private final EntityStatus status;

    private ImmutableEntityEntry(EntityStatus status) {
        this.status = status;
    }

    public static EntityEntry of(EntityStatus status) {
        return new ImmutableEntityEntry(status);
    }

    @Override
    public EntityStatus getStatus() {
        return status;
    }

    @Override
    public boolean isReadOnly() {
        return status == EntityStatus.READ_ONLY;
    }
}
