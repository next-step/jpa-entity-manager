package persistence.sql.entity.manager;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.Operators;
import persistence.sql.entity.persister.EntityPersister;
import persistence.sql.entity.persister.EntityPersisterImpl;

import java.util.Collections;
import java.util.List;

public class EntityManagerImpl<T, K> implements EntityManger<T, K> {

    private final Class<T> clazz;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerMapper<T> entityManagerMapper;
    private final EntityPersister<T, K> entityPersister;

    public EntityManagerImpl(final JdbcTemplate jdbcTemplate,
                             Class<T> clazz) {
        this.clazz = clazz;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerMapper = new EntityManagerMapper<>(clazz);
        this.entityPersister = new EntityPersisterImpl<>(jdbcTemplate, clazz);
    }


    @Override
    public List<T> findAll(final Class<T> clazz) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.from(entityMappingTable);
        return jdbcTemplate.query(selectQueryBuilder.toSql(), entityManagerMapper::mapper);
    }

    @Override
    public T find(final Class<T> clazz, final K id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();

        Criterion criterion = new Criterion(pkDomainType.getColumnName(), id.toString(), Operators.EQUALS);
        Criteria criteria = new Criteria(Collections.singletonList(criterion));

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.of(entityMappingTable, criteria);
        return jdbcTemplate.queryForObject(selectQueryBuilder.toSql(), entityManagerMapper::mapper);
    }

    @Override
    public void persist(final T entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        K key = (K) pkDomainType.getValue();

        T object = find(clazz, key);

        if (object == null) {
            entityPersister.insert(entity);
            return;
        }
        entityPersister.update(entity);
    }

    @Override
    public void remove(final T entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        K key = (K) pkDomainType.getValue();

        entityPersister.delete(key);
    }

    @Override
    public void removeAll(final Class<T> clazz) {
        entityPersister.deleteAll();
    }
}
