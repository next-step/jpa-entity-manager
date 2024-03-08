package persistence.entity.metadata;

import java.util.List;

public class EntityColumns {
    private List<EntityColumn> columns;

    public EntityColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public List<EntityColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public EntityColumn getIdColumn() {
        return columns.stream()
                .filter(EntityColumn::isPrimaryKey)
                .findFirst().orElse(null);
    }

    public EntityColumn getColumnByColumnName(String columnName) {
        return columns.stream()
                .filter(column -> column.getColumnName().equals(columnName))
                .findFirst().orElse(null);
    }



}
