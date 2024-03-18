package persistence.entity.persistencecontext;

import static persistence.entity.persistencecontext.Status.*;

public class EntityEntry {

    private Status status;

    public EntityEntry() {
        this.status = MANAGED;
    }

    public Status getStatus() {
        return status;
    }

    public void finishStatusUpdate() {
        this.status = MANAGED;
    }

    public void save() {
        this.status = SAVING;
    }

    public void load() {
        this.status = LOADING;
    }

    public void removeFromPersistenceContext() {
        this.status = DELETED;
    }

    public void removeFromDatabase() {
        this.status = GONE;
    }

    public void changeToReadOnly() {
        this.status = READ_ONLY;
    }

    public <T> boolean isReadOnly() {
        return this.status == READ_ONLY;
    }
}
