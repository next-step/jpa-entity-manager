package persistence.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import persistence.meta.EntityMeta;

public class SimplePersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> context = new ConcurrentHashMap<>();
    private final Map<EntityKey, Object> snapShotContext = new ConcurrentHashMap<>();
    private final EntityEntryContext entityEntryContext;

    public SimplePersistenceContext() {
        this.entityEntryContext = new EntityEntryContext();
    }


    @Override
    public Object getEntity(EntityKey entityKey) {
        return context.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        if (context.get(entityKey) == null) {
            this.getDatabaseSnapshot(entityKey, entity);
        }
        context.put(entityKey, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = EntityKey.of(entity);
        context.remove(key);
        snapShotContext.remove(key);
    }

    @Override
    public Object getDatabaseSnapshot(EntityKey entityKey, Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity.getClass());
        Object snapShot = entityMeta.createCopyEntity(entity);
        return snapShotContext.put(entityKey, snapShot);
    }

    public List<Object> getChangedEntity() {
        return context.entrySet()
                .stream()
                .filter(it -> !it.getValue().equals(snapShotContext.get(it.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    public <T> T saving(EntityPersister entityPersister, T entity) {
        EntityKey entityKey = EntityKey.of(entity);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        final Object saveEntity = entityEntry.saving(entityPersister, entity);

        addEntity(entityKey, saveEntity);
        entityEntry.managed();

        entityEntryContext.addEntityEntry(entityKey, entityEntry);

        return (T) saveEntity;
    }

    public void deleted(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        entityEntry.deleted();
        entityEntryContext.addEntityEntry(entityKey, entityEntry);

        if (getEntity(entityKey) != null) {
            removeEntity(entity);
        }
    }

    public <T> T loading(EntityLoader entityLoader, Class<T> clazz, Object id) {
        EntityKey entityKey = EntityKey.of(clazz, id);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        if (entityEntry.isManaged()) {
            return (T) getEntity(entityKey);
        }

        final T loadingEntity = entityEntry.loading(entityLoader, clazz, id);

        if (loadingEntity != null) {
            addEntity(entityKey, loadingEntity);
            entityEntry.managed();
        }

        return loadingEntity;
    }

    public <T> List<T> findAll(EntityLoader entityLoader, Class<T> tClass) {
        final List<T> findList = entityLoader.findAll(tClass);

        findList.forEach((it) -> {
                EntityEntry entityEntry = entityEntryContext.getEntityEntry(EntityKey.of(it));
                entityEntry.managed();
                addEntity(EntityKey.of(it), it);
        });

        return findList;
    }

    public void flush(EntityPersisteContext entityPersisterContenxt) {
        deleteEntity(entityPersisterContenxt);
        dutyCheck(entityPersisterContenxt);
        clear();
    }

    private void deleteEntity(EntityPersisteContext entityPersisterContenxt) {
        entityEntryContext.getDeletedEntityKey()
                .forEach((it) -> {
                    EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(it.getClazz());
                    entityPersister.deleteByKey(it);
                    entityEntryContext
                            .getEntityEntry(it)
                            .gone();
                });
        entityEntryContext.clear();
    }

    private void dutyCheck(EntityPersisteContext entityPersisterContenxt) {
        getChangedEntity().forEach((it) -> {
            EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(it.getClass());
            entityPersister.update(it);
        });
    }

    private void clear() {
        context.clear();
        snapShotContext.clear();
    }

}
