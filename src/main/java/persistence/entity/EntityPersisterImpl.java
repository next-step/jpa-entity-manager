package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.model.EntityColumn;
import persistence.model.EntityFactory;
import persistence.model.EntityTable;
import persistence.model.exception.ColumnInvalidException;
import persistence.model.exception.ColumnNotFoundException;
import persistence.sql.dml.DmlQueryBuilder;

import java.util.List;
import java.util.Map;

public class EntityPersisterImpl implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, DmlQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public void update(Object entity) {
        EntityTable table = EntityFactory.createPopulatedSchema(entity);

        if (!table.isPrimaryColumnsValueSet()) {
            throw new ColumnInvalidException("Primary Column is Required to Find Updating Record.");
        }

        String tableName = table.getName();

        List<Map.Entry<String, Object>> updatingKeyValues = table.getColumns().stream()
                .map(EntityColumn::toKeyValue)
                .toList();

        String sql = dmlQueryBuilder.buildUpdateQuery(
                tableName,
                updatingKeyValues,
                getDefaultPrimaryColumnKeyValue(table)
        );
        jdbcTemplate.execute(sql);
    }

    @Override
    public void insert(Object entity) {
        EntityTable table = EntityFactory.createPopulatedSchema(entity);

        String tableName = table.getName();

        List<Map.Entry<String, Object>> insertingColumns = table.getActiveColumns(table).stream()
                .map(EntityColumn::toKeyValue)
                .toList();

        String sql = dmlQueryBuilder.buildInsertQuery(tableName, insertingColumns);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void delete(Object entity) {
        EntityTable table = EntityFactory.createPopulatedSchema(entity);

        if (!table.isPrimaryColumnsValueSet()) {
            throw new ColumnInvalidException("Primary Column is Required to Find Deleting Record.");
        }

        String tableName = table.getName();
        String sql = dmlQueryBuilder.buildDeleteQuery(tableName, getDefaultPrimaryColumnKeyValue(table));
        jdbcTemplate.execute(sql);
    }

    private Map.Entry<String, Object> getDefaultPrimaryColumnKeyValue(EntityTable table) {
        // as-is 복합키는 고려 X
        return table.getPrimaryColumns().stream()
                .map(EntityColumn::toKeyValue)
                .findFirst()
                .orElseThrow(() -> new ColumnNotFoundException("Primary Column Not Found"));
    }
}
