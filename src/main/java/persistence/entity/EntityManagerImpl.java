package persistence.entity;

import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.usecase.SetFieldValueUseCase;
import persistence.sql.vo.DatabaseField;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;
    private final SetFieldValueUseCase setFieldValueUseCase;
    private final Map<Class<?>, PersistenceContext<?>> persistenceContextMap = new ConcurrentHashMap<>();


    public EntityManagerImpl(EntityLoader entityLoader, EntityPersister entityPersister,
                             GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase,
                             GetFieldValueUseCase getFieldValueUseCase,
                             SetFieldValueUseCase setFieldValueUseCase) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
        this.setFieldValueUseCase = setFieldValueUseCase;
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
        T entity = entityLoader.find(clazz, Id);
        if (entity != null) {
            persistenceContext.addEntity(Id, entity);
            persistenceContext.getDatabaseSnapshot(Id, entity);
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Object idValue = getFieldValueUseCase.execute(entity, getIdDatabaseFieldUseCase.execute(entity.getClass()));
        if (idValue == null) {
            return insertIfNotExist(entity);
        }
        T findEntity = (T) find(entity.getClass(), (Long) idValue);
        if (findEntity == null) {
            return insertIfNotExist(entity);
        } else {
            return updatetIfAlreadyExist(entity, (Long) idValue);
        }
    }


    @Override
    public <T> void remove(T entity) {
        entityPersister.delete(entity);
        Class<T> cls = (Class<T>) entity.getClass();
        PersistenceContext<T> persistenceContext = getPersistenceContext(cls);
        persistenceContext.removeEntity(entity);
    }

    private <T> PersistenceContext<T> getPersistenceContext(Class<T> cls) {
        if (persistenceContextMap.containsKey(cls)) {
            return (PersistenceContext<T>) persistenceContextMap.get(cls);
        }
        PersistenceContext<T> newContext = new PersistenceContextImpl<T>(getIdDatabaseFieldUseCase, getFieldValueUseCase);
        persistenceContextMap.put(cls, newContext);
        return newContext;
    }

    private <T> T updatetIfAlreadyExist(T entity, Long idValue) {
        entityPersister.update(entity);
        PersistenceContext<T> persistenceContext = getPersistenceContext((Class<T>) entity.getClass());
        persistenceContext.addEntity(idValue, entity);
        return entity;
    }

    private <T> T insertIfNotExist(T entity) {
        Long id = entityPersister.insert(entity);
        PersistenceContext<T> persistenceContext = getPersistenceContext((Class<T>) entity.getClass());
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        setFieldValueUseCase.execute(entity, databaseField, id);
        persistenceContext.addEntity(id, entity);
        return entity;
    }
}
