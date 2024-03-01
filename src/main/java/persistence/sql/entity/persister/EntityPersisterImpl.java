package persistence.sql.entity.persister;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;

import java.util.Collections;

public class EntityPersisterImpl<T, K> implements EntityPersister<T, K> {

    private final JdbcTemplate jdbcTemplate;
    private final Class<T> clazz;

    public EntityPersisterImpl(final JdbcTemplate jdbcTemplate,
                               final Class<T> clazz) {
        this.jdbcTemplate = jdbcTemplate;
        this.clazz = clazz;
    }

    @Override
    public boolean update(T entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        DomainType pkDomainTypes = entityMappingTable.getPkDomainTypes();
        Criterion criterion = Criterion.of(pkDomainTypes.getColumnName(), pkDomainTypes.getValue().toString());
        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.of(entityMappingTable, new Criteria(Collections.singletonList(criterion)));

        return updateExecute(updateQueryBuilder.toSql());
    }

    private boolean updateExecute(final String sql) {
        try {
            jdbcTemplate.execute(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void insert(T entity) {
        final InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.from(entity);
        jdbcTemplate.execute(insertQueryBuilder.toSql());
    }

    @Override
    public void delete(K key) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.from(entityMappingTable.getTableName());

        jdbcTemplate.execute(deleteQueryBuilder.toSql());
    }
}
