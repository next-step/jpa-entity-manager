package persistence.entity;

public class EntityEntry {
    private final EntityKey entityKey;
    private EntityStatus entityStatus;

    public EntityEntry(EntityKey entityKey) {
        this.entityKey = entityKey;
    }

    public Object saving(EntityPersister persister, Object entity) {
        entityStatus = EntityStatus.SAVING;
        entity = persister.insert(entity);
        return entity;
    }

    public void managed(PersistenceContext persistenceContext, Object entity) {
        persistenceContext.addEntity(entityKey, entity);
        entityStatus = EntityStatus.MANAGED;
    }

    public boolean isSaving() {
        return entityStatus == EntityStatus.SAVING;
    }

    public boolean isManaged() {
        return entityStatus == EntityStatus.MANAGED;
    }

    public EntityKey getEntityKey() {
        return entityKey;
    }

}
