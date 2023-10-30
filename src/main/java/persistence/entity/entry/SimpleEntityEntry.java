package persistence.entity.entry;

import java.util.Objects;

public class SimpleEntityEntry implements EntityEntry {
    private Status status;

    public SimpleEntityEntry(Status status) {
        this.status = status;
    }

    @Override
    public void updateStatus(Status toStatus) {
        this.status = toStatus;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEntityEntry)) return false;
        SimpleEntityEntry that = (SimpleEntityEntry) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
