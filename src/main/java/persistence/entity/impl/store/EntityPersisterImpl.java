package persistence.entity.impl.store;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.WherePredicate;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.statement.DeleteStatementBuilder;
import persistence.sql.dml.statement.InsertStatementBuilder;
import persistence.sql.dml.statement.UpdateStatementBuilder;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class EntityPersisterImpl implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    @Override
    public void store(Object entity, ColumnType columnType) {
        final InsertStatementBuilder insertStatementBuilder = new InsertStatementBuilder(columnType);
        final String insertSql = insertStatementBuilder.insert(entity);
        jdbcTemplate.execute(insertSql);
    }

    @Override
    public boolean update(Object entity, ColumnType columnType) {
        final String updateSql = UpdateStatementBuilder.builder()
            .update(entity, columnType)
            .equalById()
            .build();
        return jdbcTemplate.executeUpdate(updateSql);
    }

    @Override
    public void delete(Object entity, ColumnType columnType) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entity.getClass(), columnType);
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, classMappingMeta);

        final String deleteSql = DeleteStatementBuilder.builder()
            .delete(entity.getClass(), columnType)
            .where(WherePredicate.of(objectMappingMeta.getIdColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();
        jdbcTemplate.execute(deleteSql);
    }


}
