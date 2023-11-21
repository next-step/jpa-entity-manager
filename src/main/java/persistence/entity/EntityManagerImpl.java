package persistence.entity;

import persistence.entity.context.PersistenceContext;
import persistence.entity.entry.EntityEntry;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.usecase.SetFieldValue;
import persistence.sql.vo.DatabaseField;

public class EntityManagerImpl implements EntityManager {
    private final EntityEntry entityEntry;
    private final PersistenceContext persistenceContext;
    private final GetIdDatabaseField getIdDatabaseField = new GetIdDatabaseField(new GetFieldFromClass());
    private final GetFieldValue getFieldValue = new GetFieldValue();
    private final SetFieldValue setFieldValue = new SetFieldValue();


    public EntityManagerImpl(EntityEntry entityEntry,
                             PersistenceContext persistenceContext) {
        this.entityEntry = entityEntry;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T>clazz, Long Id) {
        return entityEntry.find(clazz, Id);
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

        return updatetIfAlreadyExist(entity, (Long) idValue);

    }

    @Override
    public  void remove(Object entity) {
        entityEntry.delete(entity);
    }

    @Override
    public void clear() {
        persistenceContext.clear();
    }

    private Object updatetIfAlreadyExist(Object entity, Long idValue) {
        return entityEntry.update(entity, idValue);
    }

    private <T> T insertIfNotExist(T entity) {
        Long id = entityEntry.insert(entity);
        DatabaseField databaseField = getIdDatabaseField.execute(entity.getClass());
        setFieldValue.execute(entity, databaseField, id);
        return entity;
    }
}
