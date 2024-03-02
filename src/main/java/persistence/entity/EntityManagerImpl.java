package persistence.entity;

import jakarta.persistence.GenerationType;
import jdbc.JdbcTemplate;
import org.h2.mvstore.tx.Transaction;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {

    private final Dialect dialect;
    private final PersistenceContext persistContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this(dialect, new HibernatePersistContext(), new EntityPersisterImpl(jdbcTemplate, dialect), new EntityLoaderImpl(jdbcTemplate, dialect));
    }

    public EntityManagerImpl(Dialect dialect, PersistenceContext persistContext, EntityPersister entityPersister, EntityLoader entityLoader) {
        this.dialect = dialect;
        this.persistContext = persistContext;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityMetaData entityMetaData = new EntityMetaData(clazz, new Columns(clazz.getDeclaredFields(), dialect));
        Object entity = persistContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    T findEntity = entityLoader.find(clazz, id);
                    savePersistence(findEntity, id);
                    return findEntity;
                });
        persistContext.getDatabaseSnapshot(entityMetaData, id);
        return clazz.cast(entity);
    }

    @Override
    public <T> T persist(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);
        GenerationType generationType = idColumn.getIdGeneratedStrategy().getGenerationType();
        if (dialect.getIdGeneratedStrategy(generationType).isAutoIncrement()) {
            long id = entityPersister.insertByGeneratedKey(entity);
            savePersistence(entity, id);
            setIdValue(entity, getIdField(entity, idColumn), id);
            return (T) entity;
        }

        savePersistence(entity, idColumn.getValue());
        entityPersister.insert(entity);

        return (T) entity;
    }

    private void setIdValue(Object entity, Field idField, long idValue) {
        try {
            idField.set(entity, idValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getIdField(Object entity, IdColumn idColumn) {
        Field idField;
        try {
            idField = entity.getClass().getDeclaredField(idColumn.getName());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        idField.setAccessible(true);
        return idField;
    }

    @Override
    public void remove(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);
        persistContext.removeEntity(entity.getClass(), idColumn.getValue());
        entityPersister.delete(entity, idColumn.getValue());
    }

    @Override
    public <T> T merge(T entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);
        EntityMetaData entityMetaData = persistContext.getCachedDatabaseSnapshot(entity.getClass(), idColumn.getValue());
        if (entityMetaData.isDirty(new EntityMetaData(entity, dialect))) {
            savePersistence(entity, idColumn.getValue());
            entityPersister.update(entity, idColumn.getValue());
            return entity;
        }
        savePersistence(entity, idColumn.getValue());
        return entity;
    }

    private void savePersistence(Object entity, Long id) {
        persistContext.getDatabaseSnapshot(new EntityMetaData(entity, dialect), id);
        persistContext.addEntity(entity, id);
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }
}
