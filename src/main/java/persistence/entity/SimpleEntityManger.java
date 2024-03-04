package persistence.entity;

import jakarta.persistence.EntityExistsException;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.Objects;

public class SimpleEntityManger implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManger(EntityPersister persister, EntityLoader loader) {
        this.persister = persister;
        this.loader = loader;
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        Object cachedEntity = persistenceContext.getEntity((Long) id);
        if (cachedEntity != null) {
            return (T) cachedEntity;
        }

        T findEntity = loader.read(clazz, id);
        persistenceContext.addEntity((Long) id, findEntity);
        return findEntity;
    }

    @Override
    public void persist(Object entity) {
        if (isExist(entity)) {
            throw new EntityExistsException();
        }
        Object id = persister.create(entity);
        persistenceContext.addEntity((Long) id, entity);
    }

    @Override
    public void merge(Object entity) {
        if (!isExist(entity)) {
            Object id = persister.create(entity);
            persistenceContext.addEntity((Long) id, entity);
            return;
        }

        if (isDirty(entity)) {
            Object id = persister.update(entity);
            persistenceContext.addEntity((Long) id, entity);
        }
    }

    private boolean isExist(Object entity) {
        return persistenceContext.isCached(entity) || loader.isExist(entity);
    }

    private boolean isDirty(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = new Table(clazz);

        Object id = getEntityId(entity);
        Object snapshot = persistenceContext.getDatabaseSnapshot((Long) id, entity);
        if (snapshot == null) {
            snapshot = find(clazz, id);
        }

        EntityBinder entityBinder = new EntityBinder(entity);
        EntityBinder snapshotBinder = new EntityBinder(snapshot);

        return table.getColumns()
                .stream()
                .anyMatch(column -> {
                    Object entityValue = entityBinder.getValue(column);
                    Object snapshotValue = snapshotBinder.getValue(column);
                    return !Objects.equals(entityValue, snapshotValue);
                });
    }

    private Object getEntityId(Object entity) {
        Class<?> clazz = entity.getClass();
        Table table = new Table(clazz);

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        return entityBinder.getValue(pkColumn);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        persister.delete(entity);
    }
}
