package persistence.entity;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.query.SelectQuery;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final PersistenceContext context;
    private final EntityPersister persister;

    public DefaultEntityManager(Connection connection, Dialect dialect) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.dialect = dialect;
        this.context = new DefaultPersistenceContext();
        this.persister = new DefaultEntityPersister();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        Optional<T> entity = context.getEntity(id, clazz);
        if (entity.isPresent()) {
            return entity.get();
        }

        SelectQuery query = new SelectQuery(clazz);
        String queryString = SelectQueryBuilder.builder()
                .select(query.columnNames())
                .from(query.tableName())
                .where(List.of(new WhereCondition("id", "=", id)))
                .build();
        return jdbcTemplate.queryForObject(queryString, new EntityRowMapper<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        context.addEntity(entity);
        persister.insert(entity, jdbcTemplate);
    }

    @Override
    public void remove(Object entity) {
        context.removeEntity(entity);
        persister.delete(entity, jdbcTemplate);
    }

    @Override
    public Dialect getDialect() {
        return this.dialect;
    }

}
