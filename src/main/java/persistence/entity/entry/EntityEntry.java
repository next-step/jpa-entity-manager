package persistence.entity.entry;

public interface EntityEntry {
    void updateStatus(Status toStatus);

    Status getStatus();
}
