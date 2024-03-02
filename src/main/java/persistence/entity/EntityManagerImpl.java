package persistence.entity;

import jakarta.persistence.GenerationType;
import jdbc.JdbcTemplate;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {

    private final Dialect dialect;
    private final PersistenceContext persistContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.dialect = dialect;
        this.persistContext = new HibernatePersistContext();
        this.entityLoader = new EntityLoaderImpl(jdbcTemplate, dialect);
        this.entityPersister = new EntityPersisterImpl(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistContext.getEntity(id)
                .orElseGet(() -> {
                    T findEntity = entityLoader.find(clazz, id);
                    savePersistence(findEntity, id);
                    return findEntity;
                });
        persistContext.getDatabaseSnapshot(id, entity);
        return clazz.cast(entity);

    }

    @Override
    public Object persist(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);

        GenerationType generationType = idColumn.getIdGeneratedStrategy().getGenerationType();
        if (!dialect.getIdGeneratedStrategy(generationType).isAutoIncrement()) {
            savePersistence(entity, idColumn.getValue());
            entityPersister.insert(entity);
            return entity;
        }

        if (idColumn.isNull()) {
            setIdValue(entity, getIdField(entity, idColumn), 1L);
        }

        savePersistence(entity, idColumn.getValue());
        entityPersister.insert(entity);

        return entity;
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
        persistContext.removeEntity(idColumn.getValue());
        entityPersister.delete(entity, idColumn.getValue());
    }

    @Override
    public <T> T merge(T entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);

        Object cachedDatabaseSnapshot = persistContext.getCachedDatabaseSnapshot(idColumn.getValue());
        if (!cachedDatabaseSnapshot.equals(entity)) {
            savePersistence(entity, idColumn.getValue());
            entityPersister.update(entity, idColumn.getValue());
            return entity;
        }
        savePersistence(entity, idColumn.getValue());
        return entity;
    }

    private <T> void savePersistence(T entity, Long id) {
        persistContext.getDatabaseSnapshot(id, entity);
        persistContext.addEntity(id, entity);
    }

    @Override
    public PersistenceContext getPersistContext() {
        return persistContext;
    }

    @Override
    public <T> T getSnapshot(T id) {
        return persistContext.getCachedDatabaseSnapshot(id);
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }
}
