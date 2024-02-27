package database.sql.util.column;

import database.sql.util.type.TypeConverter;

import java.lang.reflect.Field;

public interface EntityColumn {

    Field getField();

    String getFieldName();

    Object getValue(Object entity);

    String getColumnName();

    String toColumnDefinition(TypeConverter typeConverter);

    boolean isPrimaryKeyField();
}
