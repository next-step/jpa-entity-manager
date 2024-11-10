package persistence.sql.dml;

import java.util.List;
import persistence.sql.ddl.EntityTableMetadata;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class UpdateQuery implements SqlQuery {

    public String generateQuery(Object entity, List<String> updateColumns) throws IllegalAccessException {
        String tableName = new EntityTableMetadata(entity.getClass()).getTableName();
        List<ColumnValue> values = new ColumnValues<>(entity).getValuesByColumns(updateColumns);
        Long id = new EntityId(entity).getId();

        return generateUpdateQuery(tableName, values, id);
    }

    private String generateUpdateQuery(
        String tableName,
        List<ColumnValue> values,
        Long id
    ) {
        return new QueryBuilder()
            .update(tableName)
            .setValues(values)
            .where("id = " + id)
            .build();
    }

}
