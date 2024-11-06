package persistence.sql.dml;

import java.util.List;
import persistence.sql.ddl.EntityTableMetadata;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class UpdateQuery implements SqlQuery {

    public String generateQuery(Object entity, List<String> changedColumns) throws IllegalAccessException {
        List<ColumnValue> changedValues = new ColumnValues<>(entity).getValuesByColumns(changedColumns);
        String tableName = new EntityTableMetadata(entity.getClass()).getTableName();

        Long id = new EntityId(entity).getId();

        return generateUpdateQuery(tableName, changedValues, id);
    }

    private String generateUpdateQuery(
        String tableName,
        List<ColumnValue> changedValues,
        Long id
    ) {
        return new QueryBuilder()
            .update(tableName)
            .setValues(changedValues)
            .where("id = " + id)
            .build();
    }

}
