package persistence.entity;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.mapper.GenericRowMapper;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.entityPersister = new EntityPersisterImpl(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {

        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);
        SelectQueryBuilder build = queryBuilder.build(clazz);
        String query = build.findById(id);
        return jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz, dialect));
    }

    @Override
    public Object persist(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);

        GenerationType generationType = idColumn.getIdGeneratedStrategy().getGenerationType();
        if (!dialect.getIdGeneratedStrategy(generationType).isAutoIncrement()) {
            entityPersister.insert(entity);
            return entity;
        }

        if (idColumn.isNull()) {
            setIdValue(entity, getIdField(entity, idColumn), 1L);
        }

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
        entityPersister.delete(entity, idColumn);
    }

    @Override
    public boolean update(Object entity) {
        IdColumn idColumn = new IdColumn(entity, dialect);

        return entityPersister.update(entity, idColumn);
    }
}
