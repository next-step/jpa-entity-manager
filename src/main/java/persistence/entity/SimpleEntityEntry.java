package persistence.entity;

import pojo.EntityStatus;

public class SimpleEntityEntry implements EntityEntry {

    private EntityStatus entityStatus;

    public SimpleEntityEntry(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    @Override
    public void preFind() {
        checkDeletedEntity();
        this.entityStatus = EntityStatus.LOADING;
    }

    @Override
    public void postFind() {
        this.entityStatus = EntityStatus.MANAGED;
    }

    @Override
    public void prePersist() {
        checkReadOnlyStatus();
        this.entityStatus = EntityStatus.SAVING;
    }

    @Override
    public void postPersist() {
        this.entityStatus = EntityStatus.MANAGED;
    }

    @Override
    public void preUpdate() {
        checkReadOnlyStatus();
        this.entityStatus = EntityStatus.SAVING;
    }

    @Override
    public void postUpdate() {
        this.entityStatus = EntityStatus.MANAGED;
    }

    @Override
    public void preRemove() {
        checkReadOnlyStatus();
        this.entityStatus = EntityStatus.DELETED;
    }

    @Override
    public void postRemove() {
        this.entityStatus = EntityStatus.GONE;
    }

    @Override
    public void preReadOnly() {
        this.entityStatus = EntityStatus.READ_ONLY;
    }

    @Override
    public void postReadOnly() {
        this.entityStatus = EntityStatus.MANAGED;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    private void checkReadOnlyStatus() {
        if (this.entityStatus.equals(EntityStatus.READ_ONLY)) {
            throw new IllegalStateException("read only 상태에서는 insert, update, delete 수행이 불가합니다.");
        }
    }

    private void checkDeletedEntity() {
        if (this.entityStatus.equals(EntityStatus.GONE)) {
            throw new IllegalStateException("object not found exception");
        }
    }
}
