package persistence.entity;


import persistence.entity.exception.ObjectNotFoundException;

import static persistence.entity.EntityStatus.*;

public class SimpleEntityEntry implements EntityEntry {

    private EntityStatus status;

    public SimpleEntityEntry() {
        this.status = LOADING;
    }

    @Override
    public void prePersist() {
        validateReadOnlyViolation();
        validateDeleteOrGoneViolation();
        this.status = SAVING;
    }

    @Override
    public void postPersist() {
        this.status = MANAGED;
    }

    @Override
    public void preRemove() {
        validateReadOnlyViolation();
        this.status = DELETED;
    }

    @Override
    public void postRemove() {
        this.status = GONE;
    }

    @Override
    public void preUpdate() {
        validateReadOnlyViolation();
        validateDeleteOrGoneViolation();
    }

    @Override
    public void postUpdate() {
        this.status = MANAGED;
    }

    @Override
    public void preLoad() {
        validateDeleteOrGoneViolation();
        this.status = LOADING;
    }

    @Override
    public void postLoad() {
        this.status = MANAGED;
    }

    private void validateReadOnlyViolation() {
        if (status.equals(READ_ONLY)) {
            throw new IllegalStateException("read-only status not permitted to update/insert/delete");
        }
    }

    private void validateDeleteOrGoneViolation() {
        if (status.equals(DELETED) || status.equals(GONE)) {
            throw new ObjectNotFoundException("deleted/gone status not permitted to update/loading/saving");
        }
    }
}
