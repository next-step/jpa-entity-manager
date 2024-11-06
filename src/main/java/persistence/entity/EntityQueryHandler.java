package persistence.entity;

import static persistence.sql.dml.QueryTypes.DELETE;
import static persistence.sql.dml.QueryTypes.FIND_BY_ID;
import static persistence.sql.dml.QueryTypes.INSERT;
import static persistence.sql.dml.QueryTypes.UPDATE;

import java.util.List;
import jdbc.EntityLoader;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.FindByIdQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SqlQueries;
import persistence.sql.dml.UpdateQuery;

public class EntityQueryHandler<T> {

    private final JdbcTemplate jdbcTemplate;
    private final SqlQueries sqlQueries = new SqlQueries();
    private final EntityLoader<T> entityLoader;

    public EntityQueryHandler(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sqlQueries.addSqlQuery(FIND_BY_ID, new FindByIdQuery(entityClass));
        sqlQueries.addSqlQuery(UPDATE, new UpdateQuery());
        sqlQueries.addSqlQuery(INSERT, new InsertQuery());
        sqlQueries.addSqlQuery(DELETE, new DeleteQuery());
        this.entityLoader = new EntityLoader<>(entityClass);
    }

    public T findById(Object primaryKey) {
        FindByIdQuery findByIdQuery = sqlQueries.getSqlQuery(FIND_BY_ID);
        String sql = findByIdQuery.generateQuery(primaryKey);
        return jdbcTemplate.queryForObject(sql, entityLoader);
    }

    public void update(Object entity, List<String> changedColumns) throws IllegalAccessException {
        UpdateQuery updateQuery = sqlQueries.getSqlQuery(UPDATE);
        String sql = updateQuery.generateQuery(entity, changedColumns);
        jdbcTemplate.execute(sql);
    }

    public void insert(Object entity) throws IllegalAccessException {
        InsertQuery insertQuery = sqlQueries.getSqlQuery(INSERT);
        String sql = insertQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

    public void delete(Object entity) {
        DeleteQuery deleteQuery = sqlQueries.getSqlQuery(DELETE);
        String sql = deleteQuery.generateQuery(entity);
        jdbcTemplate.execute(sql);
    }

}
