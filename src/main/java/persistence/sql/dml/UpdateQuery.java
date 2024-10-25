package persistence.sql.dml;

import java.util.Map;
import java.util.stream.Collectors;
import persistence.sql.ddl.ColumnName;
import persistence.sql.ddl.ColumnType;
import persistence.sql.ddl.EntityTableMetadata;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class UpdateQuery implements SqlQuery {

    public String generateQuery(Object entity) throws IllegalAccessException {
        String tableName = new EntityTableMetadata(entity.getClass()).getTableName();
        String columns = getColumns(new EntityTableMetadata(entity.getClass()).getColumnDefinitions());
        String values = new ValueClause<>(entity).getClause();
        Long id = new EntityId(entity).getId();

        return generateUpdateQuery(tableName, columns, values, id);
    }

    private String generateUpdateQuery(
        String tableName,
        String columns,
        String values,
        Long id
    ) {
        return new QueryBuilder()
            .update(tableName)
            .columns(columns)
            .values(values)
            .where("id = " + id)
            .build();
    }

    private String getColumns(Map<ColumnName, ColumnType> columnDefinitions) {
        return columnDefinitions.keySet().stream()
            .map(ColumnName::getColumnName)
            .filter(columnName -> !columnName.equalsIgnoreCase("id"))
            .collect(Collectors.joining(", "));
    }

}
