package database.sql.dml;

import database.sql.util.EntityMetadata;
import database.sql.util.column.EntityColumn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnValueMap {

    private final Map<String, Object> columnValueMap;

    public ColumnValueMap(Object entity) {
        columnValueMap = extractValues(entity);
    }

    public Map<String, Object> getColumnValueMap() {
        return columnValueMap;
    }

    private Map<String, Object> extractValues(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        List<EntityColumn> generalColumns = entityMetadata.getGeneralColumns();
        return generalColumns.stream()
                .collect(Collectors.toMap(EntityColumn::getColumnName, it -> it.getValue(entity)));
    }
}
