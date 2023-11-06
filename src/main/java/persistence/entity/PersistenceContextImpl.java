package persistence.entity;

import domain.Snapshot;

import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityEntry entityEntry;
    private final EntityPersistenceContext entityContext;
    private final EntitySnapshot snapshotMap;

    PersistenceContextImpl() {
        this.entityEntry = new EntityEntry();
        this.entityContext = new EntityPersistenceContext();
        this.snapshotMap = new EntitySnapshot();
    }

    @Override
    public <T, I> T getEntity(Integer hashCode, EntityPersister<T> persister, I input) {
        if (entityEntry.isManaged(hashCode)) {
            return (T) entityContext.getEntity(hashCode).getObject();
        }

        entityEntry.loading(hashCode);

        Snapshot snapshot = getDatabaseSnapshot(hashCode, persister, input);

        if (snapshot.getObject() == null) {
            entityEntry.clear(hashCode);
            return null;
        }

        addEntity(hashCode, snapshot);

        entityEntry.managed(hashCode);

        return (T) snapshot.getObject();
    }

    @Override
    public Object addEntity(Integer hashCode, Object id, Object entity) {
        if (entity == null) {
            return null;
        }

        addEntity(hashCode, new Snapshot(id, entity));

        return entityContext.getEntity(hashCode).getObject();
    }

    @Override
    public Object addEntity(Integer hashCode, Snapshot snapshot) {
        entityEntry.saving(hashCode);

        return entityContext.save(hashCode, snapshot);
    }

    @Override
    public void removeEntity(Integer hashCode) {
        entityEntry.deleted(hashCode);

        entityContext.delete(hashCode);
    }

    @Override
    public <T, I> Snapshot getDatabaseSnapshot(Integer hashCode, EntityPersister<T> persister, I input) {
        Object data = persister.findById(input);
        return snapshotMap.save(hashCode, input, data);
    }

    @Override
    public Map<Integer, Snapshot> comparison() {
        if (snapshotMap.size() >= entityContext.size()) {
            return snapshotMap.exploreInSnapshot(entityContext);
        }

        return entityContext.exploreInContext(snapshotMap);
    }

    @Override
    public void flush(Map<String, EntityPersister<?>> persister) {
        Map<Integer, Snapshot> map = comparison();

        if (map.isEmpty()) {
            return;
        }

        entityEntry.getEntry().forEach((hashcode, status) -> {
            Object object = map.get(hashcode).getObject();
            EntityPersister<?> entityPersister = persister.get(object.getClass().getName());

            if (status.isSaving()) {
                entityPersister.insert(object);
                entityEntry.managed(hashcode);
                return;
            }

            if (status.isDeleted()) {
                entityPersister.delete(object);
                entityEntry.gone(hashcode);
                return;
            }

            entityPersister.update(object, map.get(entityContext.getEntity(hashcode)).getId());
            entityEntry.managed(hashcode);
        });

        clear();
    }

    private void clear() {
        entityContext.clear();
        snapshotMap.clear();
        entityEntry.clear();
    }
}
