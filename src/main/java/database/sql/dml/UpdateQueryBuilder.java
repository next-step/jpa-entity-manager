package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.column.EntityColumn;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static database.sql.Util.quote;

public class UpdateQueryBuilder {
    private final String tableName;
    private final List<EntityColumn> generalColumns;

    public UpdateQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.generalColumns = entityMetadata.getGeneralColumns();
    }

    public UpdateQueryBuilder(Class<?> clazz) {
        this(EntityMetadata.fromClass(clazz));
    }

    public String buildQuery(long id, Map<String, Object> changes) {
        return String.format("UPDATE %s SET %s WHERE %s",
                             tableName,
                             setClauses(changes),
                             whereClauses(id));
    }

    private String setClauses(Map<String, Object> changes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (EntityColumn generalColumn : generalColumns) {
            String key = generalColumn.getColumnName();
            if (changes.containsKey(key)) {
                joiner.add(String.format("%s = %s", key, quote(changes.get(key))));
            }
        }
        return joiner.toString();
    }

    private String whereClauses(long id) {
        return String.format("id = %d", id);
    }
}
