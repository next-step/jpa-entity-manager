package persistence.sql.entity.loader;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;

public class EntityLoaderImpl<T> implements EntityLoader<T> {

    private final JdbcTemplate jdbcTemplate;
    private final EntityLoaderMapper<T> entityLoaderMapper;
    private final SelectQueryBuilder selectQueryBuilder;

    public EntityLoaderImpl(final JdbcTemplate jdbcTemplate,
                            final EntityLoaderMapper<T> entityLoaderMapper,
                            final SelectQueryBuilder selectQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoaderMapper = entityLoaderMapper;
        this.selectQueryBuilder = selectQueryBuilder;
    }

    @Override
    public List<T> findAll(Class<T> clazz) {
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        String sql = selectQueryBuilder.toSql(whereClause);

        return jdbcTemplate.query(sql, entityLoaderMapper::mapper);
    }

    @Override
    public T find(Class<T> clazz, Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final WhereClause whereClause = new WhereClause(criteria);

        final String sql = selectQueryBuilder.toSql(whereClause);

        return jdbcTemplate.queryForObject(sql, entityLoaderMapper::mapper);
    }
}
