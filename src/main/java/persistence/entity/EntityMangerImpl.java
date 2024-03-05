package persistence.entity;

import jdbc.RowMapper;
import persistence.Person;
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
        Object entity = persistenceContext.getEntity(id);
        if(entity != null) {
            return (T) entity;
        }
        return entityLoader.find(clazz, id);
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
