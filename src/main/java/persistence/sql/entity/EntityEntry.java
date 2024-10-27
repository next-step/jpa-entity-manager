package persistence.sql.entity;

import java.io.Serializable;

public class EntityEntry {
    private EntityStatus entityStatus;
    private Serializable id;

    public EntityEntry(EntityStatus entityStatus, Serializable id) {
        this.entityStatus = entityStatus;
        this.id = id;
    }

    public void updateStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public Serializable getId() {
        return id;
    }
}
