package database.mapping.column;

import database.dialect.Dialect;

import java.lang.reflect.Field;

public interface EntityColumn {

    Field getField();

    String getFieldName();

    Object getValue(Object entity);

    String getColumnName();

    String toColumnDefinition(Dialect dialect);

    boolean isPrimaryKeyField();
}
