package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;
import persistence.sql.dbms.Dialect;
import persistence.sql.dml.DeleteDMLQueryBuilder;
import persistence.sql.dml.InsertDMLQueryBuilder;
import persistence.sql.dml.UpdateDMLQueryBuilder;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.Operator;
import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

public class EntityPersister<E> {
    private final JdbcTemplate jdbcTemplate;
    private final EntityTable<E> entityTable;
    private final EntityColumn<E, ?> idColumn;
    private final Dialect dialect;

    public EntityPersister(Class<E> clazz, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityTable = new EntityTable<>(clazz);
        this.idColumn = entityTable.getIdColumn();
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public E update(E entity) {
        validatePersisted(entity);

        UpdateDMLQueryBuilder<E> updateDMLQueryBuilder = new UpdateDMLQueryBuilder<>(dialect, entity)
                .where(WhereClause.of(idColumn.getDbColumnName(), idColumn.getValue(entity), Operator.EQUALS));

        jdbcTemplate.execute(updateDMLQueryBuilder.build());

        return entity;
    }

    public Object insert(E entity) {
        InsertDMLQueryBuilder<?> insertDMLQueryBuilder = new InsertDMLQueryBuilder<>(dialect, entity);

        return jdbcTemplate.executeWithGeneratedKey(insertDMLQueryBuilder.build());
    }

    public void delete(E entity) {
        validatePersisted(entity);

        DeleteDMLQueryBuilder<?> deleteDMLQueryBuilder = new DeleteDMLQueryBuilder<>(dialect, entity.getClass())
                .where(WhereClause.of(idColumn.getDbColumnName(), idColumn.getValue(entity), Operator.EQUALS));

        jdbcTemplate.execute(deleteDMLQueryBuilder.build());
    }

    private void validatePersisted(E entity) {
        if (isNotPersisted(entity)) {
            throw new IllegalStateException("id column value is null, entity = " + entity.getClass().getSimpleName());
        }
    }

    private boolean isNotPersisted(E entity) {
        return idColumn.getValue(entity) == null;
    }
}
