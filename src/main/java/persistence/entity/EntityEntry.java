package persistence.entity;

import java.util.List;
import java.util.Objects;

public class EntityEntry {
    private static final List<EntityStatus> PERSISTABLE_STATUSES = List.of(EntityStatus.GONE);
    private static final List<EntityStatus> REMOVABLE_STATUSES = List.of(EntityStatus.MANAGED);

    private EntityStatus entityStatus;

    public EntityEntry(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public void updateStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityEntry that = (EntityEntry) o;
        return entityStatus == that.entityStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityStatus);
    }

    public boolean isPersistable() {
        return PERSISTABLE_STATUSES.contains(entityStatus);
    }

    public boolean isRemovable() {
        return REMOVABLE_STATUSES.contains(entityStatus);
    }
}
