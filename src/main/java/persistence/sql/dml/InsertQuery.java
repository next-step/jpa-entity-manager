package persistence.sql.dml;

import java.util.Map;
import java.util.stream.Collectors;
import persistence.sql.ddl.ColumnName;
import persistence.sql.ddl.ColumnType;
import persistence.sql.ddl.EntityTableMetadata;
import persistence.sql.ddl.ValidateEntity;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class InsertQuery implements SqlQuery {

    public String generateQuery(Object entity) throws IllegalAccessException {
        new ValidateEntity(entity.getClass());
        String tableName = new EntityTableMetadata(entity.getClass()).getTableName();
        String columns = getColumns(new EntityTableMetadata(entity.getClass()).getColumnDefinitions());
        String values = new ValueClause<>(entity).getClause();

        return generateInsertQuery(tableName, columns, values);
    }

    private String generateInsertQuery(String tableName, String columns, String values) {
        return new QueryBuilder()
            .insertInto(tableName)
            .columns(columns)
            .values(values)
            .build();
    }

    private String getColumns(Map<ColumnName, ColumnType> columnDefinitions) {
        return columnDefinitions.keySet().stream()
            .map(ColumnName::getColumnName)
            .filter(columnName -> !columnName.equalsIgnoreCase("id"))
            .collect(Collectors.joining(", "));
    }

}
