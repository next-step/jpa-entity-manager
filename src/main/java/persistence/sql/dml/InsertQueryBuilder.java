package persistence.sql.dml;

import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.util.stream.Collectors;

public class InsertQueryBuilder {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public InsertQueryBuilder(EntityTable entityTable, EntityColumns entityColumns) {
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public String getInsertQuery(Object object) {
        String tableName = entityTable.getTableName();
        String tableColumns = columnsClause();
        String tableValues = valueClause(object);
        return String.format("insert into %s (%s) VALUES (%s)", tableName, tableColumns, tableValues);
    }

    private String columnsClause() {
        return entityColumns.getColumns().stream()
                .filter(column -> !column.isGeneratedValue())
                .filter(column -> !column.isTransient())
                .map(EntityColumn::getColumnName)
                .collect(Collectors.joining(", "));
    }

    private String valueClause(Object object) {
        return entityColumns.getColumns().stream()
                .filter(column -> !column.isGeneratedValue())
                .filter(column -> !column.isTransient())
                .map(column -> column.getFieldValue(object))
                .collect(Collectors.joining(", "));
    }
}
