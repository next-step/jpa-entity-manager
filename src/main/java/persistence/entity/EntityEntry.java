package persistence.entity;

import java.util.Objects;

public class EntityEntry {
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
}
