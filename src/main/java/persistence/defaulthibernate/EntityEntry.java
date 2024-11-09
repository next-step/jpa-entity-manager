package persistence.defaulthibernate;

public class EntityEntry {
    private EntryStatus status;
    private final Object[] loadedState;
    private final Long id;
    private final Class<?> entityClass;

    public EntityEntry(EntryStatus status, Object entity, Long id, Object[] loadedState) {
        validateAndUpdate(status);
        this.status = status;
        this.id = id;
        this.entityClass = entity.getClass();
        this.loadedState = loadedState;
    }

    private void validateAndUpdate(EntryStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
    }

    public void setStatus(EntryStatus status) {
        validateAndUpdate(status);
        this.status = status;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public Object[] getLoadedState() {
        // 조회 시점에 방어적 복사
        return loadedState.clone();
    }
    public Long getId() {
        return id;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
