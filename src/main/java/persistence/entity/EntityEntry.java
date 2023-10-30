package persistence.entity;

import java.util.Objects;

public class EntityEntry {

    private final Object entity;
    private Status status;


    public EntityEntry(final Object entity, final Status status) {
        this.entity = entity;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void updateStatus(final Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityEntry that = (EntityEntry) object;
        return Objects.equals(entity, that.entity) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, status);
    }
}
