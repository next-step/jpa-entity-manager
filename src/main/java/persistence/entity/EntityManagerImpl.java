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
        T entity = entityLoader.find(clazz, Id);
        if(entity != null) {
            PersistenceContext<T> persistenceContext = getPersistenceContext(clazz);
            persistenceContext.addEntity(Id, entity);
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Object idValue = getFieldValueUseCase.execute(entity, getIdDatabaseFieldUseCase.execute(entity.getClass()));
        PersistenceContext<T> persistenceContext = getPersistenceContext((Class<T>) entity.getClass());
        // id가 있는 경우
        if (idValue != null) {
            T findEntity = (T) find(entity.getClass(), (Long) idValue);
            if(findEntity == null) { // 키를 가지고 넣어주는 경우
                entityPersister.insert(entity);
                persistenceContext.addEntity((Long) idValue, entity);
                return entity;
            } else { // 이미 존재하는 데이터
                entityPersister.update(entity);
                persistenceContext.addEntity((Long) idValue, entity);
                return findEntity;
            }
        }
        // id가 없는 경우
        Long id = entityPersister.insert(entity);
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        setFieldValueUseCase.execute(entity, databaseField, id);
        persistenceContext.addEntity(id, entity);
        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        Class<T> cls = (Class<T>) entity.getClass();
        PersistenceContext<T> persistenceContext = getPersistenceContext(cls);
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }

    private <T> PersistenceContext<T> getPersistenceContext(Class<T> cls) {
        if(persistenceContextMap.containsKey(cls)) {
            return (PersistenceContext<T>) persistenceContextMap.get(cls);
        }
        PersistenceContext<T> newContext = new PersistenceContextImpl<T>(getIdDatabaseFieldUseCase, getFieldValueUseCase);
        persistenceContextMap.put(cls, newContext);
        return newContext;
    }
}
