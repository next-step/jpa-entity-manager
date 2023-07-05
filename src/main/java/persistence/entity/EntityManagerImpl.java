package persistence.entity;

import jakarta.persistence.Id;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.exception.NoIdentifierException;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = createPersistenceContext();
    }

    private PersistenceContext createPersistenceContext() {
        return new PersistenceContextImpl();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistenceContext.getEntity(id);
        if (entity != null) {
            return (T) entity;
        }

        SelectQueryBuilder builder = new SelectQueryBuilder(clazz);
        String query = builder.buildFindByIdQuery(id);
        T foundEntity = jdbcTemplate.queryForObject(query, new EntityRowMapper<>(clazz));
        persistenceContext.addEntity(id, foundEntity);

        return foundEntity;
    }

    @Override
    public void persist(Object entity) {
        InsertQueryBuilder builder = new InsertQueryBuilder(entity.getClass());
        String query = builder.build(entity);
        Long id = jdbcTemplate.executeAndGetGeneratedKey(query);
        setGeneratedKey(entity, id);
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    private void setGeneratedKey(Object entity, Long id) {
        try {
            Field idField = getIdField(entity.getClass());
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getIdField(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Id.class))
            .findFirst()
            .orElseThrow(() -> new NoIdentifierException(entityClass.getSimpleName()));
    }

    @Override
    public void remove(Object entity) {
        Long id = findPrimaryKey(entity);
        DeleteQueryBuilder builder = new DeleteQueryBuilder(entity.getClass());
        String query = builder.buildDeleteByIdQuery(id);
        jdbcTemplate.execute(query);
        persistenceContext.removeEntity(id);
    }

    private Long findPrimaryKey(Object entity) {
        try {
            Field idField = getIdField(entity.getClass());
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isNew(Object entity) {
        return findPrimaryKey(entity) == null;
    }

    @Override
    public <T> T merge(T entity) {
        Long id = findPrimaryKey(entity);
        UpdateQueryBuilder builder = new UpdateQueryBuilder(Person.class);
        jdbcTemplate.execute(builder.build(id, entity));
        persistenceContext.addEntity(id, entity);
        return entity;
    }
}
