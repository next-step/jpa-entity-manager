package persistence.entity;

import persistence.exception.ObjectNotFoundException;

public class EntityEntry {
    private EntityKey entityKey;
    private EntityStatus entityStatus;

    public EntityEntry(EntityKey entityKey) {
        this.entityKey = entityKey;
        this.entityStatus = EntityStatus.NEW;
    }

    public void readOnly() {
        entityStatus = EntityStatus.READ_ONLY;
    }

    public Object saving(EntityPersister persister, Object entity) {
        if (entityStatus.isReadOnly()) {
            throw new IllegalStateException("읽기 전용 상태입니다.");
        }

        entityStatus = EntityStatus.SAVING;
        entity = persister.insert(entity);
        entityKey = EntityKey.of(entity);
        return entity;
    }

    public void managed(PersistenceContext persistenceContext, Object entity) {
        persistenceContext.addEntity(entityKey, entity);
        entityStatus = EntityStatus.MANAGED;
    }

    public <T> T loading(EntityLoader entityLoader, Class<T> tClass, Object id) {
        if (entityStatus.isGone()) {
            throw new ObjectNotFoundException();
        }
        entityStatus = EntityStatus.LOADING;
        return entityLoader.find(tClass, id);
    }

    public void deleted(PersistenceContext persistenceContext, Object entity) {
        if (entityStatus.isReadOnly()) {
            throw new IllegalStateException("읽기 전용 상태입니다.");
        }

        entityStatus = EntityStatus.DELETED;
        if (persistenceContext.getEntity(entityKey) != null) {
            persistenceContext.removeEntity(entity);
        }

        entityStatus = EntityStatus.GONE;
    }

    public boolean isManaged() {
        return entityStatus.isManaged();
    }

    public boolean isGone() {
        return entityStatus.isGone();
    }


    public EntityKey getEntityKey() {
        return entityKey;
    }
}
