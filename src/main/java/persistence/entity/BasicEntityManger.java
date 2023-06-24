package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.context.BasicPersistentContext;
import persistence.context.PersistenceContext;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class BasicEntityManger implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;
    private final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.INSTANCE;
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;

    public BasicEntityManger(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new BasicPersistentContext();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistenceContext.getEntity(id);
        if (entity != null) {
            return (T) entity;
        }

        return selectPersistentContextEntity(clazz, id);
    }

    private <T> T selectPersistentContextEntity(Class<T> clazz, Long id) {
        String selectQuery = selectQueryBuilder.findById(clazz, id);
        T selectedEntity = jdbcTemplate.queryForObject(selectQuery, new RowMapperImpl<>(clazz));
        persistenceContext.addEntity(id, selectedEntity);
        return selectedEntity;
    }

    @Override
    public void persist(Object entity) {
        String insertQuery = insertQueryBuilder.insert(entity);
        jdbcTemplate.execute(insertQuery);
        addPersistentContextEntity(entity, getSavedEntity(entity));
    }

    private Object getSavedEntity(Object entity) {
        String selectLastSavedQuery = selectQueryBuilder.findFirst(entity.getClass());
        return jdbcTemplate.queryForObject(selectLastSavedQuery, new RowMapperImpl<>(entity.getClass()));
    }

    private void addPersistentContextEntity(Object entity, Object savedEntity) {
        EntityFields entityFields = EntityFields.of(entity.getClass());
        EntityField idField = entityFields.getId();
        Object idFieldValue = idField.getValue(savedEntity);
        idField.setValue(entity, idFieldValue);
        persistenceContext.addEntity((Long) idFieldValue, entity);
    }

    @Override
    public void remove(Object entity) {
        String deleteQuery = deleteQueryBuilder.delete(entity);
        jdbcTemplate.execute(deleteQuery);
        removePersistentContextEntity(entity);
    }

    private void removePersistentContextEntity(Object entity) {
        EntityField idField = EntityFields.of(entity.getClass()).getId();
        persistenceContext.removeEntity((Long) idField.getValue(entity));
    }
}
