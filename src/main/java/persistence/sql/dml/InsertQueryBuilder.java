package persistence.sql.dml;

import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.util.stream.Collectors;

public class InsertQueryBuilder {

    public String getInsertQuery(EntityTable entityTable, EntityColumns entityColumns, Object object) {
        String tableName = entityTable.getTableName();
        String tableColumns = columnsClause(entityColumns);
        String tableValues = valueClause(entityColumns,object);
        return String.format("insert into %s (%s) VALUES (%s)", tableName, tableColumns, tableValues);
    }

    private String columnsClause(EntityColumns entityColumns) {
        return entityColumns.getColumns().stream()
                .filter(column -> !column.isGeneratedValue())
                .filter(column -> !column.isTransient())
                .map(EntityColumn::getColumnName)
                .collect(Collectors.joining(", "));
    }

    private String valueClause(EntityColumns entityColumns, Object object) {
        return entityColumns.getColumns().stream()
                .filter(column -> !column.isGeneratedValue())
                .filter(column -> !column.isTransient())
                .map(column -> column.getFieldValue(object))
                .collect(Collectors.joining(", "));
    }
}
