package persistence.entity;

import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.context.PersistenceContextMap;
import persistence.entity.entry.EntityEntry;
import persistence.sql.usecase.CreateSnapShotObject;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.usecase.SetFieldValue;
import persistence.sql.vo.DatabaseField;

public class EntityManagerImpl implements EntityManager {
    private final EntityEntry entityEntry;
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValue getFieldValue;
    private final SetFieldValue setFieldValue;
    private final CreateSnapShotObject createSnapShotObject;
    private final PersistenceContextMap persistenceContextMap;


    public EntityManagerImpl(EntityEntry entityEntry,
                             PersistenceContextMap persistenceContextMap,
                             GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase,
                             GetFieldValue getFieldValue,
                             SetFieldValue setFieldValue,
                             CreateSnapShotObject createSnapShotObject) {
        this.entityEntry = entityEntry;
        this.persistenceContextMap = persistenceContextMap;
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValue = getFieldValue;
        this.setFieldValue = setFieldValue;
        this.createSnapShotObject = createSnapShotObject;
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        if (Id == null) {
            throw new NullPointerException("Id can't be null in EntityManager find");
        }
        PersistenceContext<T> persistenceContext = getPersistenceContext(clazz);
        T persistenceContextCachedEntity = persistenceContext.getEntity(Id);
        if (persistenceContextCachedEntity != null) {
            return persistenceContextCachedEntity;
        }
        T entity = entityEntry.find(clazz, Id);
        if (entity != null) {
            persistenceContext.addEntity(Id, entity);
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Object idValue = getFieldValue.execute(entity, getIdDatabaseFieldUseCase.execute(entity.getClass()));
        if (idValue == null) {
            return insertIfNotExist(entity);
        }
        T findEntity = (T) find(entity.getClass(), (Long) idValue);
        if (findEntity == null) {
            return insertIfNotExist(entity);
        }

        return updatetIfAlreadyExist(entity, (Long) idValue);

    }


    @Override
    public <T> void remove(T entity) {
        entityEntry.delete(entity);
        Class<T> cls = (Class<T>) entity.getClass();
        PersistenceContext<T> persistenceContext = getPersistenceContext(cls);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void clear() {
        persistenceContextMap.clear();
    }

    private <T> PersistenceContext<T> getPersistenceContext(Class<T> cls) {
        if (persistenceContextMap.containsKey(cls)) {
            return (PersistenceContext<T>) persistenceContextMap.get(cls);
        }
        PersistenceContext<T> newContext = new PersistenceContextImpl<T>(getIdDatabaseFieldUseCase, getFieldValue, createSnapShotObject);
        persistenceContextMap.put(cls, newContext);
        return newContext;
    }

    private <T> T updatetIfAlreadyExist(T entity, Long idValue) {
        PersistenceContext<T> persistenceContext = getPersistenceContext((Class<T>) entity.getClass());
        T snapshotEntity = persistenceContext.getDatabaseSnapshot(idValue);
        if (entity.equals(snapshotEntity)) {
            return entity;
        }
        if (entityEntry.update(entity)) {
            persistenceContext.addEntity(idValue, entity);
        }
        return entity;
    }

    private <T> T insertIfNotExist(T entity) {
        Long id = entityEntry.insert(entity);
        PersistenceContext<T> persistenceContext = getPersistenceContext((Class<T>) entity.getClass());
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        setFieldValue.execute(entity, databaseField, id);
        persistenceContext.addEntity(id, entity);
        return entity;
    }
}
