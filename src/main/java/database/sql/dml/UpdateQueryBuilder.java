package database.sql.dml;

import database.sql.util.EntityMetadata;
import database.sql.util.column.EntityColumn;

import java.util.List;
import java.util.StringJoiner;

import static database.sql.Util.quote;

// XXX: test
public class UpdateQueryBuilder {
    private final String tableName;
    private final List<EntityColumn> generalColumns;

    public UpdateQueryBuilder(Class<?> entityClass) {
        EntityMetadata metadata = new EntityMetadata(entityClass);

        this.tableName = metadata.getTableName();
        this.generalColumns = metadata.getGeneralColumns();
    }

    public String buildQuery(long id, Object entity) {
        return String.format("UPDATE %s SET %s WHERE %s",
                             tableName,
                             setClauses(entity),
                             whereClauses(id));
    }

    private String setClauses(Object entity) {
        StringJoiner joiner = new StringJoiner(", ");
        for (EntityColumn generalColumn : generalColumns) {
            String key = generalColumn.getColumnName();
            Object value = generalColumn.getValue(entity);

            joiner.add(String.format("%s = %s", key, quote(value)));
        }
        return joiner.toString();
    }

    private String whereClauses(long id) {
        return String.format("id = %d", id);
    }
}
