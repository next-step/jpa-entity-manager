package persistence.model;

import jakarta.persistence.Table;
import persistence.util.ReflectionUtil;

import java.util.List;
import java.util.Map;

public class EntityTable {
    private final String name;
    private final EntityTableColumns tableColumns = new EntityTableColumns();

    private EntityTable(String name) {
        this.name = name;
    }

    public static EntityTable build(Class<?> entityClass) {
        String tableName = getTableName(entityClass);

        return new EntityTable(tableName);
    }

    public static String getTableName(Class<?> clazz) {
        return ReflectionUtil.getAnnotationIfPresent(clazz, Table.class)
                .map(Table::name)
                .orElse(clazz.getSimpleName());
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

    public EntityColumn getPrimaryColumn() {
        return tableColumns.getPrimaryColumn();
    }

    public List<EntityColumn> getNonPrimaryColumns() {
        return tableColumns.getNonPrimaryColumns();
    }

    public void setColumns(List<EntityColumn> columns) {
        tableColumns.setColumns(columns);
    }

    public List<EntityColumn> getActiveColumns() {
        return isPrimaryColumnsValueSet()
                ? getColumns()
                : getNonPrimaryColumns();
    }

    public Map.Entry<String, Object> getPrimaryColumnKeyValue() {
        return getPrimaryColumn().toKeyValue();
    }

    public boolean isPrimaryColumnsValueSet() {
        return getPrimaryColumn().isValueNotNull();
    }

    public void setPrimaryValue(Object value) {
        getPrimaryColumn().setValue(value);
    }
}
