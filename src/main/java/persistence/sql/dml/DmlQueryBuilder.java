package persistence.sql.dml;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import persistence.sql.ddl.exception.NoIdentifierException;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class DmlQueryBuilder {

    protected final Class<?> entityClass;

    protected DmlQueryBuilder(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    protected String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            return getTableNameFromAnnotation(entityClass);
        }
        return entityClass.getSimpleName();
    }

    private String getTableNameFromAnnotation(Class<?> entityClass) {
        Table annotation = entityClass.getAnnotation(Table.class);
        String tableName = annotation.name();
        if (tableName.isEmpty()) {
            return entityClass.getSimpleName();
        }
        return tableName;
    }

    protected String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return getColumnNameFromAnnotation(field);
        }
        return field.getName();
    }

    private String getColumnNameFromAnnotation(Field field) {
        String name = field.getAnnotation(Column.class).name();
        if (name.isEmpty()) {
            return field.getName();
        }
        return name;
    }

    protected String whereClause(Long primaryKey) {
        Field idField = getIdField();
        return String.format(" WHERE %s = %d", getColumnName(idField), primaryKey);
    }

    protected Field getIdField() {
        return Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Id.class))
            .findFirst()
            .orElseThrow(() -> new NoIdentifierException(entityClass.getSimpleName()));
    }
}
