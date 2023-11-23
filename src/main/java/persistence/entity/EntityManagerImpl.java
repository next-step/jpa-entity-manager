package persistence.entity;

import persistence.entity.context.PersistenceContext;
import persistence.entity.entry.EntityEntry;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.usecase.SetFieldValue;
import persistence.sql.vo.DatabaseField;

public class EntityManagerImpl implements EntityManager {
    private final EntityEntry entityEntry;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final GetIdDatabaseField getIdDatabaseField = new GetIdDatabaseField(new GetFieldFromClass());
    private final GetFieldValue getFieldValue = new GetFieldValue();
    private final SetFieldValue setFieldValue = new SetFieldValue();


    public EntityManagerImpl(EntityEntry entityEntry,
                             EntityPersister entityPersister,
                             EntityLoader entityLoader,
                             PersistenceContext persistenceContext) {
        this.entityEntry = entityEntry;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T>clazz, Long id) {
        if (id == null || clazz == null) {
            throw new NullPointerException("Id can't be null in EntityManager find");
        }
        Object cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity == null) {
            Object dbEntity = entityLoader.find(clazz, id);
            if (dbEntity != null) {
                entityEntry.updateLoading(dbEntity);
                persistenceContext.addEntity(id, dbEntity);
                entityEntry.updateManaged(dbEntity);
                return (T) dbEntity;
            }
            return null;
        }
        if (entityEntry.readAble(cachedEntity)) {
            return (T) cachedEntity;
        }
        throw new IllegalStateException("Object is not readable");
    }

    @Override
    public Object persist(Object entity) {
        Object idValue = getFieldValue.execute(entity, getIdDatabaseField.execute(entity.getClass()));
        if (idValue == null) {
            return insertIfNotExist(entity);
        }
        Object findEntity = find(entity.getClass(), (Long) idValue);
        if (findEntity == null) {
            return insertIfNotExist(entity);
        }

        return updateIfAlreadyExist(entity, (Long) idValue);

    }

    @Override
    public  void remove(Object entity) {
        if (entity == null) {
            throw new NullPointerException("delete with null");
        }
        entityEntry.updateDeleted(entity);
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
        entityEntry.updateGone(entity);
    }

    @Override
    public void clear() {
        persistenceContext.clear();
    }

    private Object updateIfAlreadyExist(Object entity, Long idValue) {
        if (entity == null) {
            throw new NullPointerException("Can not be update with null");
        }
        if(!entityEntry.updateAble(entity)) {
            throw new IllegalStateException("Can not be update");
        }
        Object snapshotEntity = persistenceContext.getDatabaseSnapshot(entity.getClass(), idValue);
        if (entity.equals(snapshotEntity)) {
            return entity;
        }
        entityPersister.update(entity);
        persistenceContext.addEntity(idValue, entity);
        return entity;
    }

    private <T> T insertIfNotExist(T entity) {
        if (entity == null) {
            throw new NullPointerException("insert with null");
        }
        if (!entityEntry.insertAble(entity)) {
            throw new IllegalStateException("Can not be insert");
        }
        entityEntry.updateSaving(entity);
        Long id = entityPersister.insert(entity);
        persistenceContext.addEntity(id, entity);
        entityEntry.updateManaged(entity);
        DatabaseField databaseField = getIdDatabaseField.execute(entity.getClass());
        setFieldValue.execute(entity, databaseField, id);
        return entity;
    }
}
