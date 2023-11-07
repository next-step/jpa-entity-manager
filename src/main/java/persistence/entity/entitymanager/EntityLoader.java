package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;
import jdbc.SimpleEntityRowMapper;
import persistence.sql.dbms.Dialect;
import persistence.sql.dml.SelectDMLQueryBuilder;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.Operator;
import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public T findById(Class<T> clazz, Long id) {
        EntityTable<T> entityTable = new EntityTable<>(clazz);
        EntityColumn<T, ?> idColumn = entityTable.getIdColumn();

        SelectDMLQueryBuilder<T> selectDMLQueryBuilder = new SelectDMLQueryBuilder<>(dialect, clazz)
                .where(WhereClause.of(idColumn.getDbColumnName(), id, Operator.EQUALS));
        selectDMLQueryBuilder.build();

        return jdbcTemplate.queryForObject(selectDMLQueryBuilder.build(), new SimpleEntityRowMapper<>(entityTable, dialect));
    }
}
