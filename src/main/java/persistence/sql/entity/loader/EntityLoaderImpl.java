package persistence.sql.entity.loader;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityLoaderMapper entityLoaderMapper;
    private final SelectQueryBuilder selectQueryBuilder;

    public EntityLoaderImpl(final JdbcTemplate jdbcTemplate,
                            final EntityLoaderMapper entityLoaderMapper,
                            final SelectQueryBuilder selectQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoaderMapper = entityLoaderMapper;
        this.selectQueryBuilder = selectQueryBuilder;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTableName(),
                columnClause,
                whereClause);

        return jdbcTemplate.query(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        final WhereClause whereClause = new WhereClause(criteria);

        final String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTableName(),
                columnClause,
                whereClause);

        return jdbcTemplate.queryForObject(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }
}
