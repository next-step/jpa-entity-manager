package persistence.entity;

import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;

public class EntityMangerImpl implements EntityManger {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityMangerImpl(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object cachedEntity = persistenceContext.getEntity(id);
        if(cachedEntity != null) {
            return (T) cachedEntity;
        }
        T foundEntity = entityLoader.find(clazz, id);
        persistenceContext.addEntity(id, foundEntity);

        return foundEntity;
    }

    @Override
    public Object persist(Object entity) {
        boolean isEntityUpdated = entityPersister.update(entity);
        if (!isEntityUpdated) {
            entityPersister.insert(entity);
        }

        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();
        persistenceContext.addEntity((Long)keyColumn.getValue(), entity);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
