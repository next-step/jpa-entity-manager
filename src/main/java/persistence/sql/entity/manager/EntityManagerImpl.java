package persistence.sql.entity.manager;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.Operators;
import persistence.sql.entity.model.PrimaryDomainType;
import persistence.sql.entity.persister.EntityPersister;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityManagerImpl<T> implements EntityManger<T> {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerMapper<T> entityManagerMapper;
    private final SelectQueryBuilder selectQueryBuilder;
    private final EntityPersister<T> entityPersister;


    public EntityManagerImpl(final JdbcTemplate jdbcTemplate,
                             final EntityManagerMapper<T> entityManagerMapper,
                             final SelectQueryBuilder selectQueryBuilder,
                             final EntityPersister<T> entityPersister) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerMapper = entityManagerMapper;
        this.selectQueryBuilder = selectQueryBuilder;
        this.entityPersister = entityPersister;
    }

    @Override
    public List<T> findAll(final Class<T> clazz) {
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        String sql = selectQueryBuilder.toSql(whereClause);

        return jdbcTemplate.query(sql, entityManagerMapper::mapper);
    }

    @Override
    public T find(final Class<T> clazz, final Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final WhereClause whereClause = new WhereClause(criteria);

        final String sql = selectQueryBuilder.toSql(whereClause);

        return jdbcTemplate.queryForObject(sql, entityManagerMapper::mapper);
    }

    @Override
    public void persist(final T entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        final T object = find((Class<T>) entity.getClass(), key);

        if (object == null) {
            entityPersister.insert(entity);
            return;
        }
        entityPersister.update(entity);
    }

    @Override
    public void remove(final T entity) {
        entityPersister.delete(entity);
    }

    @Override
    public void removeAll(final Class<T> clazz) {
        entityPersister.deleteAll();
    }
}
