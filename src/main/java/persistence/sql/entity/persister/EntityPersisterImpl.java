package persistence.sql.entity.persister;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.exception.InvalidUpdateSqlException;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.UpdateColumnClause;
import persistence.sql.dml.query.clause.ValueClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.NormalDomainType;
import persistence.sql.entity.model.PrimaryDomainType;
import persistence.sql.entity.model.TableName;

import java.util.Collections;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;


    public EntityPersisterImpl(final JdbcTemplate jdbcTemplate,
                               final InsertQueryBuilder insertQueryBuilder,
                               final UpdateQueryBuilder updateQueryBuilder,
                               final DeleteQueryBuilder deleteQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
    }

    @Override
    public boolean update(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        WhereClause whereClause = new WhereClause(Criteria.fromPkCriterion(primaryDomainType));

        UpdateColumnClause updateColumnClause = UpdateColumnClause.from(entityMappingTable.getDomainTypes());

        return jdbcTemplate.execute(updateQueryBuilder.toSql(entityMappingTable.getTableName(), updateColumnClause, whereClause));
    }

    @Override
    public void insert(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(entity.getClass());

        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        final ValueClause valueClause = ValueClause.from(entity, entityMappingTable.getDomainTypes());

        jdbcTemplate.executeUpdate(insertQueryBuilder.toSql(entityMappingTable.getTableName(), columnClause, valueClause));
    }

    @Override
    public Object insertWithPk(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(entity.getClass());

        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        final ValueClause valueClause = ValueClause.from(entity, entityMappingTable.getDomainTypes());

        return jdbcTemplate.executeAndReturnKey(insertQueryBuilder.toSql(entityMappingTable.getTableName(), columnClause, valueClause));
    }

    @Override
    public void delete(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();

        final WhereClause whereClause = new WhereClause(Criteria.fromPkCriterion(primaryDomainType));

        jdbcTemplate.executeUpdate(deleteQueryBuilder.toSql(entityMappingTable.getTableName(), whereClause));
    }

    @Override
    public void deleteAll(Class<?> clazz) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        jdbcTemplate.execute(deleteQueryBuilder.toSql(entityMappingTable.getTableName(), whereClause));
    }
}
