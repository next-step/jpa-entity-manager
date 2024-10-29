package jpa;

public class EntityEntry {

    private EntityStatus entityStatus;

    public EntityEntry(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public void updateStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }
}
