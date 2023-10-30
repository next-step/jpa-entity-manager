package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.SimpleEntityRowMapper;
import persistence.sql.dbms.Dialect;
import persistence.sql.dml.SelectDMLQueryBuilder;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.Operator;
import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

public class JdbcEntityManager implements EntityManager {
    private JdbcTemplate jdbcTemplate;
    private Dialect dialect;

    public JdbcEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityTable<T> entityTable = new EntityTable<>(clazz);
        EntityColumn<T, ?> idColumn = entityTable.getIdColumn();

        SelectDMLQueryBuilder<T> selectDMLQueryBuilder = new SelectDMLQueryBuilder<>(dialect, clazz)
                .where(WhereClause.of(idColumn.getDbColumnName(), id, Operator.EQUALS));
        selectDMLQueryBuilder.build();

        return jdbcTemplate.queryForObject(selectDMLQueryBuilder.build(), new SimpleEntityRowMapper<>(entityTable, dialect));
    }

    public <T> T findByEntity(T entity) {
        Class<T> entityClass = (Class<T>) entity.getClass();
        EntityTable<T> entityTable = (EntityTable<T>) new EntityTable<>(entity.getClass());
        EntityColumn<T, ?> idColumn = entityTable.getIdColumn();

        SelectDMLQueryBuilder<T> selectDMLQueryBuilder = new SelectDMLQueryBuilder<>(dialect, entityClass)
                .where(WhereClause.of(idColumn.getDbColumnName(), idColumn.getValue(entity), Operator.EQUALS));
        selectDMLQueryBuilder.build();

        return jdbcTemplate.queryForObject(selectDMLQueryBuilder.build(), new SimpleEntityRowMapper<>(entityTable, dialect));
    }

    @Override
    public void persist(Object entity) {
        EntityPersister<Object> entityPersister = createEntityPersister(entity.getClass());
        Object persistedEntity = findByEntity(entity);

        if (persistedEntity != null) {
            entityPersister.update(entity);
            return;
        }

        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        EntityPersister<Object> entityPersister = createEntityPersister(entity.getClass());

        entityPersister.delete(entity);
    }

    private EntityPersister<Object> createEntityPersister(Class<?> clazz) {
        /**
         * TODO 매번 EntityPersister의 인스턴스를 생성하지 않고, 캐싱할 수 있도록 수정 필요
         * (일단 지금은 매번 생성하도록 구현)
         */

        return (EntityPersister<Object>) new EntityPersister<>(clazz, jdbcTemplate, dialect);
    }
}
