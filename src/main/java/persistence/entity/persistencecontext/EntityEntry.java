package persistence.entity.persistencecontext;

public interface EntityEntry {
    EntityStatus getStatus();
    void updateStatus(EntityStatus status);
}
