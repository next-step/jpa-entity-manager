package persistence.model;

import jakarta.persistence.Table;
import persistence.model.util.ReflectionUtil;

import java.util.List;

public class EntityTable {
    private final String name;

    private final EntityTableColumns tableColumns = new EntityTableColumns();

    private EntityTable(String name) {
        this.name = name;
    }

    public static EntityTable build(Class<?> entityClass) {
        String tableName = ReflectionUtil.getTableName(entityClass);

        return new EntityTable(tableName);
    }

    public String getName() {
        return name;
    }

    public List<EntityColumn> getColumns() {
        return tableColumns.getAll();
    }

    public EntityColumn getColumn(String name) {
        return tableColumns.findByName(name);
    }

    public List<EntityColumn> getPrimaryColumns() {
        return tableColumns.getPrimaryColumns();
    }

    public List<EntityColumn> getNonPrimaryColumns() {
        return tableColumns.getNonPrimaryColumns();
    }

    public void setColumns(List<EntityColumn> columns) {
        tableColumns.setColumns(columns);
    }

    public List<EntityColumn> getActiveColumns(EntityTable table) {
        return table.isPrimaryColumnsValueSet()
                ? table.getColumns()
                : table.getNonPrimaryColumns();
    }

    public Boolean isPrimaryColumnsValueSet() {
        return getPrimaryColumns().stream().allMatch(EntityColumn::isValueNotNull);
    }
}
