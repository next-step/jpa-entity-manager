package jpa;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;
import persistence.sql.entity.EntityRowMapper;
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
            SelectQuery selectQuery = SelectQuery.getInstance();
            persistenceContext.add(
                    entityInfo,
                    jdbcTemplate.queryForObject(selectQuery.findById(clazz, id), new EntityRowMapper<>(clazz))
            );
        }

        return clazz.cast(persistenceContext.get(entityInfo));
    }

    @Override
    public void update(Object entity) {
        UpdateQuery updateQuery = UpdateQuery.getInstance();
        jdbcTemplate.execute(updateQuery.makeQuery(entity));
        addPersistenceContext(entity);
    }


    @Override
    public void insert(Object entity) {
        InsertQuery insertQuery = InsertQuery.getInstance();
        jdbcTemplate.execute(insertQuery.makeQuery(entity));
        addPersistenceContext(entity);
    }

    private void addPersistenceContext(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        String idValue = entityId.getIdValue(entity);
        persistenceContext.add(new EntityInfo<>(entity.getClass(), idValue), entity);
    }

    @Override
    public void delete(Object entity) {
        DeleteQuery deleteQuery = DeleteQuery.getInstance();
        jdbcTemplate.execute(deleteQuery.makeQuery(entity));

        EntityId entityId = new EntityId(entity.getClass());
        String idValue = entityId.getIdValue(entity);
        persistenceContext.remove(new EntityInfo<>(entity.getClass(), idValue));
    }
}
