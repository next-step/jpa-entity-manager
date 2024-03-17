package persistence.sql.entity.impl;

import java.util.Objects;

public class EntityEntry {
    private Status status;

    private EntityEntry(final Status status) {
        this.status = status;
    }

    public static EntityEntry of(Status status) {
        return new EntityEntry(status);
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EntityEntry that = (EntityEntry) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    public boolean isReadOnly() {
        return this.status.equals(Status.READ_ONLY);
    }

    public boolean isGone() {
        return this.status.equals(Status.GONE);
    }

    public boolean isNotReadOnly() {
        return !this.status.equals(Status.READ_ONLY);
    }
}
