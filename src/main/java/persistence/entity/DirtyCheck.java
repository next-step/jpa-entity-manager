package persistence.entity;

public class DirtyCheck {

    private final PersistenceContext persistenceContext;

    public DirtyCheck(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public boolean check(Object entity) {
        int snapshot = persistenceContext.getDatabaseSnapshot(entity);
        return snapshot != entity.hashCode();
    }

}
