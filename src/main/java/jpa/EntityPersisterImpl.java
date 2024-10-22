package jpa;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;
import persistence.sql.entity.EntityRowMapper;
import persistence.sql.model.EntityColumnValue;
import persistence.sql.model.EntityId;

import java.util.Objects;

public class EntityPersisterImpl implements EntityPersister {
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(PersistenceContext persistenceContext,
                               JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(Class<T> clazz, Object id) {
        EntityInfo<?> entityInfo = new EntityInfo<>(clazz, id);
        if (Objects.isNull(persistenceContext.get(entityInfo))) {
            SelectQuery selectQuery = new SelectQuery(clazz);
            persistenceContext.add(
                    entityInfo,
                    jdbcTemplate.queryForObject(selectQuery.findById(id), new EntityRowMapper<>(clazz))
            );
        }

        return clazz.cast(persistenceContext.get(entityInfo));
    }

    @Override
    public void update(Object entity) {
        UpdateQuery updateQuery = new UpdateQuery(entity);
        jdbcTemplate.execute(updateQuery.makeQuery());
        addPersistenceContext(entity);
    }


    @Override
    public void insert(Object entity) {
        InsertQuery insertQuery = new InsertQuery(entity);
        jdbcTemplate.execute(insertQuery.makeQuery());
        addPersistenceContext(entity);
    }

    private void addPersistenceContext(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        EntityColumnValue idEntityColumnValue = entityId.getIdValue(entity);
        persistenceContext.add(new EntityInfo<>(entity.getClass(), idEntityColumnValue.getValue()), entity);
    }

    @Override
    public void delete(Object entity) {
        DeleteQuery deleteQuery = new DeleteQuery(entity);
        jdbcTemplate.execute(deleteQuery.makeQuery());

        EntityId entityId = new EntityId(entity.getClass());
        EntityColumnValue idEntityColumnValue = entityId.getIdValue(entity);
        persistenceContext.remove(new EntityInfo<>(entity.getClass(), idEntityColumnValue.getValue()));
    }
}
