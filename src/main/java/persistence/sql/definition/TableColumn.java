package persistence.sql.definition;

import persistence.sql.Dialect;
import persistence.sql.Queryable;

import java.lang.reflect.Field;

public class TableColumn implements Queryable {
    private final ColumnDefinition columnDefinition;

    public TableColumn(Field field) {
        this.columnDefinition = new ColumnDefinition(field);
    }

    @Override
    public void applyToCreateTableQuery(StringBuilder query, Dialect dialect) {
        final String type = dialect.translateType(columnDefinition);
        query.append(columnDefinition.getName()).append(" ").append(type);

        if (columnDefinition.isNotNullable()) {
            query.append(" NOT NULL");
        }

        query.append(", ");
    }

    @Override
    public boolean hasValue(Object entity) {
        return columnDefinition.hasValue(entity);
    }

    @Override
    public String getValue(Object entity) {
        final Object value = columnDefinition.valueAsString(entity);

        if (value instanceof String) {
            return "'" + value + "'";
        }

        return value.toString();
    }

    @Override
    public void bindValue(Object entity, Object value) {
        columnDefinition.bindValue(entity, value);
    }

    @Override
    public String getName() {
        return columnDefinition.getName();
    }

    @Override
    public String getDeclaredName() {
        return columnDefinition.declaredName();
    }

}
