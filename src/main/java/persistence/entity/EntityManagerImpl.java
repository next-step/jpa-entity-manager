package persistence.entity;

import jakarta.persistence.GenerationType;
import jdbc.JdbcTemplate;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {

    private final Dialect dialect;
    private final HibernatePersistContext persistContext;
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
                    persistContext.addEntity(id, findEntity);
                    return findEntity;
                });
        return clazz.cast(entity);

    }

    @Override
    public Object persist(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);

        GenerationType generationType = idColumn.getIdGeneratedStrategy().getGenerationType();
        if (!dialect.getIdGeneratedStrategy(generationType).isAutoIncrement()) {
            persistContext.addEntity(idColumn.getValue(), entity);
            entityPersister.insert(entity);
            return entity;
        }

        if (idColumn.isNull()) {
            setIdValue(entity, getIdField(entity, idColumn), 1L);
        }

        persistContext.addEntity(idColumn.getValue(), entity);
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
    public boolean update(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);
        persistContext.addEntity(idColumn.getValue(), entity);
        return entityPersister.update(entity, idColumn.getValue());
    }

    @Override
    public PersistenceContext getPersistContext() {
        return persistContext;
    }
}
